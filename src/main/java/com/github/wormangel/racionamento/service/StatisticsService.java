package com.github.wormangel.racionamento.service;

import com.github.wormangel.racionamento.model.BoqueiraoConstants;
import com.github.wormangel.racionamento.service.model.AesaHistoricalVolumeData;
import com.github.wormangel.racionamento.service.model.AesaVolumeData;
import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class StatisticsService {
    @Autowired
    private AesaSpider aesaSpider;

    public BoqueiraoStatistics getStatistics() throws IOException, ParseException {
        LocalDate today = LocalDate.now();

        AesaVolumeData currentVolumeData = aesaSpider.getCurrentVolumeData();

        AesaHistoricalVolumeData historicalVolumeData = aesaSpider.getHistoricalVolumeData(
                String.format("%02d", today.getDayOfMonth()), String.format("%02d", today.getMonth().getValue()));

        double measurementsDelta = historicalVolumeData.getLastHistoricalVolumes().values()
                .stream()
                .reduce(0d, (a, b) -> b - a) / 10;

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
                .build();
    }

}
