package com.github.wormangel.racionamento.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.github.wormangel.racionamento.util.FormattingUtils.formatDouble;

@Data
@Builder
@AllArgsConstructor
public class VolumeMeasurement {
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy")
    private LocalDate date;
    private Double volume;

    @JsonGetter("volume")
    public String getVolumeString() {
        return formatDouble(volume);
    }
}
