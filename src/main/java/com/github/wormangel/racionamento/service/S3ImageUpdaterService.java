package com.github.wormangel.racionamento.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class S3ImageUpdaterService {
    @Value("${s3.bucket}")
    private String bucket;

    @Autowired
    private ImageGenerationService imageGenerationService;

    public boolean generateImageAndUpdate(int days) {
        log.info("Generating image for {} days", days);

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        // Generate the image
        BufferedImage ogImage = null;
        File destination = null;
        try {
            ogImage = imageGenerationService.getOgImage(days);

            // Create a temporary file to upload to s3
            destination = File.createTempFile("new", ".png");
            ImageIO.write(ogImage, "png", destination);
        } catch (IOException e) {
            log.error("Error generating new image! Returning!", e);
            return false;
        }

        log.info("Image generated successfully! Sending to s3...");

        // Make sure the file is publicly accessible
        PutObjectRequest s3request = new PutObjectRequest(bucket, "ogImage" + days + ".png", destination).withCannedAcl(
                CannedAccessControlList.PublicRead);

        // Send it
        try {
            s3Client.putObject(s3request);
        } catch (SdkClientException e) {
            log.error("Error uploading the image to S3!", e);
            return false;
        }

        log.info("Image sent to s3 successfully!");
        return true;
    }
}
