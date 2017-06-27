package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.ImageService;
import com.github.wormangel.racionamento.service.StatisticsService;
import com.github.wormangel.racionamento.service.spider.AesaSpider;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.ParseException;

@Controller
public class WebController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/")
    public String home(Model model) throws IOException, ParseException {
        BoqueiraoStatistics stats = statisticsService.getStatistics();
        model.addAttribute("data", stats);
        return "index";
    }

    @RequestMapping(value = "/ogimage.png")
    public ResponseEntity<Resource> ogImage() throws IOException, ParseException {
        int days = statisticsService.getStatistics().getDaysToHappiness();

        BufferedImage ogImage = imageService.getOgImage(days);

        File destination = File.createTempFile("new", ".png");
        ImageIO.write(ogImage, "png", destination);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(new FileInputStream(destination)));
    }
}
