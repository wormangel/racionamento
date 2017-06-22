package com.github.wormangel.racionamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.wormangel.racionamento.service.model.VolumeMeasurement;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

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
    private LocalDate date;

    private Double currentVolume;

    private List<VolumeMeasurement> lastHistoricalVolumes;

    // Calculated
    
    private Double percentageFull;

    private boolean over;

    private Double measurementsDeltaAverage;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy")
    private LocalDate happinessDate;
    private int daysToHappiness;

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

    @JsonGetter("happinessDate")
    public String getHappinessDate() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("pt","BR")).format(happinessDate);
    }

    @JsonGetter("date")
    public String getDate() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("pt","BR")).format(date);
    }
}
