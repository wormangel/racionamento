package net.wormangel.service;

import net.wormangel.model.BoqueiraoStatistics;
import net.wormangel.service.model.AesaVolumeData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.wormangel.model.BoqueiraoConstants.*;

@Service
public class StatisticsService {
    @Autowired
    private AesaSpider aesaSpider;

    public BoqueiraoStatistics getStatistics() throws IOException, ParseException {
        AesaVolumeData currentVolumeData = aesaSpider.getCurrentVolumeData();

        return BoqueiraoStatistics.builder()
                .maxVolume(MAX_VOLUME)
                .deadVolumeThreshold(DEAD_VOLUME_THRESHOLD)
                .rationingVolumeThreshold(RATIONING_VOLUME_THRESHOLD)
                .currentVolume(currentVolumeData.getCurrentVolume())
                .date(currentVolumeData.getMeasureDate())
                .percentageFull((currentVolumeData.getCurrentVolume() / MAX_VOLUME) * 100)
                .over( currentVolumeData.getCurrentVolume() > RATIONING_VOLUME_THRESHOLD )
                .build();
    }

}
