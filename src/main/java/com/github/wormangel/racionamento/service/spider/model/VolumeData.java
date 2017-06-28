package com.github.wormangel.racionamento.service.spider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeData {
    private VolumeMeasurement currentMeasurement;
    private List<VolumeMeasurement> historicalMeasurements = new ArrayList<>();
}
