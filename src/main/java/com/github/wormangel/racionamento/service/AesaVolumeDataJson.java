package com.github.wormangel.racionamento.service;

import lombok.Data;

import java.util.List;

@Data
public class AesaVolumeDataJson {
    private String bacia;
    private String acude;
    private Double capacidade;
    private List<AesaMeasurementJson> data;
    private int id;
    private String municipio;
}
