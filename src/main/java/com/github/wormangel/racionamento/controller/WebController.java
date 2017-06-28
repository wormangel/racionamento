package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.StatisticsService;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class WebController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "/")
    public String home(@RequestParam(name = "kill", required = false) String killCache, Model model) throws IOException, ParseException {
        if (killCache != null) {
            log.info("Page requested with cache kill parameter! Killing it...");
            cacheManager.getCache("statisticsCache").clear();
        }

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
