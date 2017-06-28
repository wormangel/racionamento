package com.github.wormangel.racionamento.service.spider;

import com.github.wormangel.racionamento.service.spider.model.VolumeData;
import com.github.wormangel.racionamento.service.spider.model.VolumeMeasurement;
import com.github.wormangel.racionamento.service.spider.model.AesaMeasurementJson;
import com.github.wormangel.racionamento.service.spider.model.AesaVolumeDataJson;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@EnableCaching
@Slf4j
public class AesaSpider {
    private static final String DATA_URL = "http://www.aesa.pb.gov.br/aesa-website/resources/data/volumeAcudes/porAcude/531/2017/data.json";

    @Autowired
    private RestTemplate restTemplate;

    public VolumeData getVolumeData() {
        // Get the response
        AesaVolumeDataJson wsResponse = restTemplate.getForEntity(DATA_URL, AesaVolumeDataJson.class).getBody();
        // Sort the collection
        Collections.sort(wsResponse.getData(), Comparator.comparing(AesaMeasurementJson::getMeasurementDate));
        // Order descending
        Collections.reverse(wsResponse.getData());

        AesaMeasurementJson currentMeasure = wsResponse.getData().get(0);

        // Our response object
        VolumeData filteredData = new VolumeData();

        filteredData.setCurrentMeasurement(new VolumeMeasurement(currentMeasure.getMeasurementDate(),
                currentMeasure.getMeasuredVolume()));

        filteredData.setHistoricalMeasurements(
                wsResponse.getData()
                .stream()
                .map(x -> new VolumeMeasurement(x.getMeasurementDate(), x.getMeasuredVolume()))
                .collect(Collectors.toList()));

        return filteredData;
    }
}
