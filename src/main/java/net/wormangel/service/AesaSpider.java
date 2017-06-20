package net.wormangel.service;

import net.wormangel.service.model.AesaVolumeData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class AesaSpider {
    private static final String CURRENT_VOLUME_URL = "http://site2.aesa.pb.gov.br/aesa/volumesAcudes.do?metodo=preparaUltimosVolumesPorAcude2";

    @Cacheable()
    public AesaVolumeData getCurrentVolumeData() throws IOException, ParseException {
        AesaVolumeData result = new AesaVolumeData();

        // Fetch the HTML page
        Document doc = Jsoup.connect(CURRENT_VOLUME_URL).get();

        // Try to find the weir - that' a new word :D - looking at the text values in the second column of the table
        Elements elements = doc.select(".tabelaInternaTituloForm tr td:nth-child(2) div font b");
        for (Element e : elements) {
            if (e.text().equals("Epitácio Pessoa")) {
                // Get the parent row
                Element tr = e.parent().parent().parent().parent();

                // Get the current volume by looking at the value in the 4th column, stripping the commas before parsing the double
                result.setCurrentVolume(Double.valueOf(tr.select("td:nth-child(4) div font b").get(0).text().replace(",", "")));

                // Get the measurement date by looking at the value in the 6th column
                result.setMeasureDate(new SimpleDateFormat("dd/MM/yyyy").parse(tr.select("td:nth-child(6) div font b").get(0).text()));
                break;
            }
        }

        return result;
    }

//    public getHistoricalVolumeData() {
//
//    }
}
