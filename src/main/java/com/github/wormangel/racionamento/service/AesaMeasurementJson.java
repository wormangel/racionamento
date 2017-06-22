package com.github.wormangel.racionamento.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class AesaMeasurementJson {
    private int acudeId;
    private double currentVolumePercentage;
    private double measuredVolume;
    private int rainMeasure;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate measurementDate;

    @JsonCreator
    public AesaMeasurementJson(ArrayList<Object> props) {
        acudeId = (Integer) props.get(0);
        currentVolumePercentage = (Double) props.get(1);
        measuredVolume = (Double) props.get(2);
        rainMeasure = (Integer) props.get(3);
        measurementDate = LocalDate.parse((String) props.get(4));
    }
}
