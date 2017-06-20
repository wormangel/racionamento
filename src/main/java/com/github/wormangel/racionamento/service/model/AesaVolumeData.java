package com.github.wormangel.racionamento.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AesaVolumeData {
    private Double currentVolume;
    private Date measureDate;
}
