package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.StatisticsService;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class WebController {
    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/")
    public String home(Model model) throws IOException, ParseException {
        BoqueiraoStatistics stats = statisticsService.getStatistics();
        model.addAttribute("data", stats);
        return "index";
    }

    @Scheduled(cron = "0 15 10 ? * *", zone = "America/Recife")
    public void triggerStatisticsUpdate() throws IOException, ParseException {
        log.info("Good morning! Scheduled task to update the statistics starting...");
        new SimpleAsyncTaskExecutor().execute(this::update);
    }

    @CacheEvict("statisticsCache")
    public void update() {
        try {
            statisticsService.getStatistics();
        } catch (Exception e) {
            log.error("Error updating statistics asynchronously!");
        }
    }
}
