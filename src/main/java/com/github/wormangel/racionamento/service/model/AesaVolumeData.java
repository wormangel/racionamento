package com.github.wormangel.racionamento.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AesaVolumeData {
    private Double currentVolume;
    private LocalDate measureDate;
}
