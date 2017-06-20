package com.github.wormangel.racionamento.service;

import com.github.wormangel.racionamento.service.model.AesaHistoricalVolumeData;
import com.github.wormangel.racionamento.service.model.AesaVolumeData;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
@EnableCaching
@Slf4j
public class AesaSpider {
    private static final String CURRENT_VOLUME_URL = "http://site2.aesa.pb.gov.br/aesa/volumesAcudes.do?metodo=preparaUltimosVolumesPorAcude2";
    private static final String DAILY_VOLUME_CURRENT_MONTH_URL = "http://site2.aesa.pb.gov.br/aesa/volumesAcudes.do?metodo=preparaVolumesDiariosAtual";
    private static final String DAILY_VOLUME_PREVIOUS_MONTH_URL = "http://site2.aesa.pb.gov.br/aesa/volumesAcudes.do?metodo=preparaVolumesDiariosAnterior";

    @Cacheable("currentVolumeDataCache")
    public AesaVolumeData getCurrentVolumeData() throws IOException, ParseException {
        log.info("Cache miss on currentVolumeDataCache. Fetching information from AESA...");

        log.info("Getting current volume data using URL {}", CURRENT_VOLUME_URL);
        AesaVolumeData result = new AesaVolumeData();

        // Fetch the HTML page
        Document doc = Jsoup.connect(CURRENT_VOLUME_URL).get();

        // Try to find the weir - that' a new word :D - looking at the text values in the second column of the table
        Elements elements = doc.select(".tabelaInternaTituloForm tr td:nth-child(2) div font b");
        for (Element e : elements) {
            if (e.text().equals("Epitácio Pessoa")) {
                // Get the parent row
                Element tr = e.parent().parent().parent().parent();

                // Get the current volume by looking at the value in the 4th column
                String currentVolumeText = tr.select("td:nth-child(4) div font b").get(0).text();
                // Get the measurement date by looking at the value in the 6th column
                String measurementDateText = tr.select("td:nth-child(6) div font b").get(0).text();

                // Strip the commas before parsing the double - here the volumes appear as full measures in cubic meters (m3),
                // but with commas separating every three digits (for readability, I assume)
                result.setCurrentVolume(Double.valueOf(currentVolumeText.replace(",", "")));
                result.setMeasureDate(new SimpleDateFormat("dd/MM/yyyy").parse(measurementDateText));
                break;
            }
        }

        return result;
    }

    @Cacheable("historicalDataCache")
    public AesaHistoricalVolumeData getHistoricalVolumeData(String day, String month) throws IOException {
        log.info("Cache miss on historicalDataCache. Fetching information from AESA...");

        AesaHistoricalVolumeData result = new AesaHistoricalVolumeData();

        return getHistoricalVolumeData(month, result, DAILY_VOLUME_CURRENT_MONTH_URL);
    }

    private AesaHistoricalVolumeData getHistoricalVolumeData(String month, AesaHistoricalVolumeData toReturn,
                                                             String url) throws IOException {
        log.info("Getting historical data for month {} using URL {}", month, url);

        // Fetch the HTML page - it' important to note we get some sort of pre-render version of the page here, where the
        // numeric values are not the same you see on a browser - maybe before some JS magic that jsoup doesn't support
        // or something (actually the same values - more precise, pre-human-readable-formatting - can be see using Postman
        // to make requests). In this case (historical volume data), the numbers have a period as a decimal separator
        // and the unit is millions of m3
        Document doc = Jsoup.connect(url).get();

        // For each row representing a day - skip the first row
        Elements elements = doc.select(".tabelaInternaTituloForm tbody tr:nth-child(n+2)");
        // Let' start from the last day, end of list
        Collections.reverse(elements);

        for (Element e : elements) {
            // Find the measurement day in the first column
            String dayText = e.select("td:nth-child(1)").text();
            // Find the measurement volume in the sixth column - this is the one for desired Boqueirão weir
            String volumeText = e.select("td:nth-child(6)").text();

            // Get the current volume by looking at the value in the 4th column, stripping the commas before parsing the double
            String mapKey = formatDateAsMapKey(dayText, month);
            // Remember the volume in these pages are in MILLIONS of m3! Different measure than the current volume page
            toReturn.getLastHistoricalVolumes().put(mapKey, Double.valueOf(volumeText) * 1000000);

            log.info("Inserted historical volume data for key {}: {}", mapKey, toReturn.getLastHistoricalVolumes().get(mapKey));

            if (toReturn.getLastHistoricalVolumes().size() == 10) {
                log.info("10 historical volumes recorded. Breaking the loop.");
                break;
            }
        }

        // We might get here with less than ten measurements - meaning we are still in the beginning of a month
        if (toReturn.getLastHistoricalVolumes().size() < 10) {
            log.info("Loop finished but historical data has las than ten entries. Sending the spider to crawl the previous month data.");
            return getHistoricalVolumeData(String.format("%02d", Integer.valueOf(month) - 1), toReturn, DAILY_VOLUME_PREVIOUS_MONTH_URL);
        }

        return toReturn;
    }

    private String formatDateAsMapKey(String day, String month) {
        // Let's hope this software doesn't run past this year :P
        return day + month + "2017";
    }


}
