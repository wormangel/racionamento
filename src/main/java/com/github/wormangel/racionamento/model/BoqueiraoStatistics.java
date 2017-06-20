package com.github.wormangel.racionamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Builder;
import lombok.Data;
import com.github.wormangel.racionamento.util.FormattingUtils;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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

    // Calculated
    
    private Double percentageFull;
    private boolean over;

    // Getters with formatting
    @JsonGetter("maxVolume")
    public String getMaxVolume() {
        return FormattingUtils.formatDouble(maxVolume);
    }

    @JsonGetter("rationingVolumeThreshold")
    public String getRationingVolumeThreshold() {
        return FormattingUtils.formatDouble(rationingVolumeThreshold);
    }

    @JsonGetter("currentVolume")
    public String getCurrentVolume() {
        return FormattingUtils.formatDouble(currentVolume);
    }

    @JsonGetter("percentageFull")
    public String getPercentageFull() {
        return FormattingUtils.formatDouble(percentageFull);
    }

    @JsonGetter("deadVolumeThreshold")
    public String getDeadVolumeThreshold() {
        return FormattingUtils.formatDouble(deadVolumeThreshold);
    }
}
