package com.github.wormangel.racionamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.wormangel.racionamento.service.model.AesaHistoricalVolumeData;
import groovy.util.MapEntry;
import lombok.Builder;
import lombok.Data;
import com.github.wormangel.racionamento.util.FormattingUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.github.wormangel.racionamento.util.FormattingUtils.formatDouble;

@Data
@Builder
public class BoqueiraoStatistics {

    // Fixed stuff
    private Double maxVolume;
    
    private Double deadVolumeThreshold;
    
    private Double rationingVolumeThreshold;

    // Updated every day
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy")
    private Date date;

    private Double currentVolume;

    private AesaHistoricalVolumeData historicalVolumeData;

    // Calculated
    
    private Double percentageFull;
    private boolean over;
    private Double measurementsDeltaAverage;

    // Getters with formatting
    @JsonGetter("maxVolume")
    public String getMaxVolume() {
        return formatDouble(maxVolume);
    }

    @JsonGetter("rationingVolumeThreshold")
    public String getRationingVolumeThreshold() {
        return formatDouble(rationingVolumeThreshold);
    }

    @JsonGetter("currentVolume")
    public String getCurrentVolume() {
        return formatDouble(currentVolume);
    }

    @JsonGetter("percentageFull")
    public String getPercentageFull() {
        return formatDouble(percentageFull);
    }

    @JsonGetter("deadVolumeThreshold")
    public String getDeadVolumeThreshold() {
        return formatDouble(deadVolumeThreshold);
    }

    @JsonGetter("measurementsDeltaAverage")
    public String getMeasurementsDeltaAverage() {
        return formatDouble(measurementsDeltaAverage);
    }

    @JsonGetter("historicalVolumeData")
    public Map<String, String> getHistoricalVolumeData() {
        Map result = new HashMap();

        historicalVolumeData.getLastHistoricalVolumes()
            .forEach((k, v) -> result.put(k, formatDouble(v)));

        return result;
    }
}
