package com.github.wormangel.racionamento.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.github.wormangel.racionamento.model.BoqueiraoConstants;
import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.model.AesaVolumeData;
import com.github.wormangel.racionamento.service.spider.AesaSpider;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class StatisticsService {
    @Autowired
    private AesaSpider aesaSpider;

    @Value("${s3.bucket}")
    private String bucket;

    @Autowired
    private ImageService imageService;

    @Cacheable("statisticsCache")
    public BoqueiraoStatistics getStatistics() throws IOException, ParseException {
        log.info("Cache miss on statisticsCache. Fetching information from AESA and recalculating Statistics...");
        // Get the volume data
        AesaVolumeData data = aesaSpider.getVolumeData();

        // Calculate the measurements delta - the rate at which the weir is filling up
        // We calculate the delta between every pair of days for the last ten days, then sum then and divide by the
        // number of measurements (10)
        double measurementsDelta = IntStream.range(0, 10)
                .mapToDouble(x -> data.getHistoricalMeasurements().get(x).getVolume() - data.getHistoricalMeasurements().get(x+1).getVolume())
                .reduce(0d, (a, b) -> a + b) / 10;

        // Calculate the predicted date at which the weir will be filled
        double accumulatedVolume = data.getCurrentMeasurement().getVolume();
        int daysToHappiness = 0;
        while (accumulatedVolume < BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD) {
            accumulatedVolume += measurementsDelta;
            daysToHappiness++;
        }

        LocalDate happinessDate = data.getCurrentMeasurement().getDate().plusDays(daysToHappiness);

        // Upload the new image to s3
        log.info("Starting task in the background to generate a new ogImage and put it in s3..");

        S3ImageUpdater updateS3ImageAsyncTask = new S3ImageUpdater(daysToHappiness);
        new SimpleAsyncTaskExecutor().execute(updateS3ImageAsyncTask);

        log.info("Task is executing in the background. Returning the statistics object.");

        return BoqueiraoStatistics.builder()
                .maxVolume(BoqueiraoConstants.MAX_VOLUME)
                .deadVolumeThreshold(BoqueiraoConstants.DEAD_VOLUME_THRESHOLD)
                .rationingVolumeThreshold(BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD)
                .currentVolume(data.getCurrentMeasurement().getVolume())
                .date(data.getCurrentMeasurement().getDate())
                .percentageFull((data.getCurrentMeasurement().getVolume() / BoqueiraoConstants.MAX_VOLUME) * 100)
                .over( data.getCurrentMeasurement().getVolume() > BoqueiraoConstants.RATIONING_VOLUME_THRESHOLD )
                .measurementsDeltaAverage(measurementsDelta)
                .lastHistoricalVolumes(data.getHistoricalMeasurements().stream().limit(10).collect(Collectors.toList()))
                .daysToHappiness(daysToHappiness)
                .happinessDate(happinessDate)
                .build();
    }

    private class S3ImageUpdater implements Runnable {
        private int days;

        public S3ImageUpdater(int value) {
            days = value;
        }

        @Override
        public void run() {
            try {
                AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

                // Create a temporary file first
                File destination = File.createTempFile("new", ".png");
                BufferedImage ogImage = imageService.getOgImage(days);
                ImageIO.write(ogImage, "png", destination);

                // Make sure the file is publicly accessible
                PutObjectRequest s3request = new PutObjectRequest(bucket, "ogImage" + days + ".png",destination).withCannedAcl(
                        CannedAccessControlList.PublicRead);
                s3Client.putObject(s3request);

                log.info("Image updated to s3 successfully!");

                log.info("Triggering Facebook Open Graph cache refresh..");
                triggerFbCacheRefresh();
            } catch (Exception e) {
                log.error("Error saving the new ogImage to s3!", e);
            }
        }

        private void triggerFbCacheRefresh() {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = UriComponentsBuilder.fromUriString("https://graph.facebook.com")
                    .queryParam("id", "http://racionamento.herokuapp.com")
                    .queryParam("scrape", "true")
                    .build()
                    .toUri();

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(uri, null, String.class);
                log.info("Open Graph API refresh call succeeded! Response code: {}", response.getStatusCode());
            } catch (Exception e) {
                log.error("Error updating Open Graph information!", e);
            }

        }
    }

}
