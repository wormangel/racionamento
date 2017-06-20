package com.github.wormangel.racionamento.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AesaHistoricalVolumeData {
    private Map<String, Double> lastHistoricalVolumes = new HashMap<>();
}
