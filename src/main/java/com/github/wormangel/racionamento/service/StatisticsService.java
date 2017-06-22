package com.github.wormangel.racionamento.service;

import com.github.wormangel.racionamento.model.BoqueiraoConstants;
import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.model.AesaHistoricalVolumeData;
import com.github.wormangel.racionamento.service.model.AesaVolumeData;
import com.github.wormangel.racionamento.service.spider.AesaSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

@Service
public class StatisticsService {
    @Autowired
    private AesaSpider aesaSpider;

    public BoqueiraoStatistics getStatistics() throws IOException, ParseException {
        LocalDate today = LocalDate.now();

        // Get the current volume data
        AesaVolumeData currentVolumeData = aesaSpider.getCurrentVolumeData();

        // Get the historical volume data (last ten days)
        AesaHistoricalVolumeData historicalVolumeData = aesaSpider.getHistoricalVolumeData(
                String.format("%02d", today.getDayOfMonth()), String.format("%02d", today.getMonth().getValue()));

        // Calculate the measurements delta - the rate at which the weir is filling up
        double measurementsDelta = historicalVolumeData.getLastHistoricalVolumes().values()
                .stream()
                .reduce(0d, (a, b) -> b - a) / 10;

        // Calculate the predicted date at which the weir will be filled
        double accumulatedVolume = currentVolumeData.getCurrentVolume();
        int daysToHappiness = 0;
        while (accumulatedVolume < BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD) {
            accumulatedVolume += measurementsDelta;
            daysToHappiness++;
        }

        LocalDate happinessDate = currentVolumeData.getMeasureDate().plusDays(daysToHappiness);

        return BoqueiraoStatistics.builder()
                .maxVolume(BoqueiraoConstants.MAX_VOLUME)
                .deadVolumeThreshold(BoqueiraoConstants.DEAD_VOLUME_THRESHOLD)
                .rationingVolumeThreshold(BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD)
                .currentVolume(currentVolumeData.getCurrentVolume())
                .date(currentVolumeData.getMeasureDate())
                .percentageFull((currentVolumeData.getCurrentVolume() / BoqueiraoConstants.MAX_VOLUME) * 100)
                .over( currentVolumeData.getCurrentVolume() > BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD )
                .measurementsDeltaAverage(measurementsDelta)
                .historicalVolumeData(historicalVolumeData)
                .daysToHappiness(daysToHappiness)
                .happinessDate(happinessDate)
                .build();
    }

}
