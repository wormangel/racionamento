package net.wormangel.service;

import net.wormangel.model.BoqueiraoStatistics;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.wormangel.model.BoqueiraoConstants.DEAD_VOLUME_THRESHOLD;
import static net.wormangel.model.BoqueiraoConstants.MAX_VOLUME;
import static net.wormangel.model.BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD;

@Component
public class StatisticsService {
    public BoqueiraoStatistics getStatistics() throws IOException, ParseException {
        double currentVolume = 26911187;
        Date date = new Date();

        Document doc = Jsoup.connect("http://site2.aesa.pb.gov.br/aesa/volumesAcudes.do?metodo=preparaUltimosVolumesPorAcude2").get();
        Elements elements = doc.select(".tabelaInternaTituloForm tr td:nth-child(2) div font b");
        for (Element e : elements) {
            if (e.text().equals("Epitácio Pessoa")) {
                Element tr = e.parent().parent().parent().parent();


                currentVolume = Double.valueOf(tr.select("td:nth-child(4) div font b").get(0).text().replace(",", ""));
                date = new SimpleDateFormat("dd/MM/yyyy").parse(tr.select("td:nth-child(6) div font b").get(0).text());
                break;
            }
        }

        return BoqueiraoStatistics.builder()
                .maxVolume(MAX_VOLUME)
                .deadVolumeThreshold(DEAD_VOLUME_THRESHOLD)
                .rationingVolumeThreshold(RATIONING_VOLUME_THRESHOLD)
                .currentVolume(currentVolume)
                .date(date)
                .percentageFull((currentVolume / MAX_VOLUME) * 100)
                .over( currentVolume > RATIONING_VOLUME_THRESHOLD )
                .build();
    }

}
