package net.wormangel.service.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
public class AesaVolumeData {
    private Double currentVolume;
    private Date measureDate;
}
