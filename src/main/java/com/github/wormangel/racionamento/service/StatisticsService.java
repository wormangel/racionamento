package com.github.wormangel.racionamento.service;

import com.github.wormangel.racionamento.model.BoqueiraoConstants;
import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.spider.AesaSpider;
import com.github.wormangel.racionamento.service.spider.model.VolumeData;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticsService {
    @Autowired
    private AesaSpider aesaSpider;

    public BoqueiraoStatistics getStatistics() {

        // Get the volume data
        VolumeData data = aesaSpider.getVolumeData();

        // Calculate the measurements delta - the rate at which the weir is filling up
        // We calculate the delta between every pair of days for the last ten days, then sum then and divide by the
        // number of measurements (10)
        double measurementsDelta = IntStream.range(0, 10)
                .mapToDouble(x -> data.getHistoricalMeasurements().get(x).getVolume() - data.getHistoricalMeasurements().get(x+1).getVolume())
                .reduce(0d, (a, b) -> a + b) / 10;

        // Calculate the predicted date at which the weir will be filled
        double accumulatedVolume = data.getCurrentMeasurement().getVolume();
        int daysToHappiness = 0;
        while (accumulatedVolume < BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD) {
            accumulatedVolume += measurementsDelta;
            daysToHappiness++;
        }

        LocalDate happinessDate = data.getCurrentMeasurement().getDate().plusDays(daysToHappiness);

        return BoqueiraoStatistics.builder()
                .maxVolume(BoqueiraoConstants.MAX_VOLUME)
                .deadVolumeThreshold(BoqueiraoConstants.DEAD_VOLUME_THRESHOLD)
                .rationingVolumeThreshold(BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD)
                .currentVolume(data.getCurrentMeasurement().getVolume())
                .date(data.getCurrentMeasurement().getDate())
                .percentageFull((data.getCurrentMeasurement().getVolume() / BoqueiraoConstants.MAX_VOLUME) * 100)
                .over( data.getCurrentMeasurement().getVolume() > BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD )
                .measurementsDeltaAverage(measurementsDelta)
                .lastHistoricalVolumes(data.getHistoricalMeasurements().stream().limit(10).collect(Collectors.toList()))
                .daysToHappiness(daysToHappiness)
                .happinessDate(happinessDate)
                .build();
    }

}
