package com.github.wormangel.racionamento.service;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BoqueiraoService {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private S3ImageUpdaterService s3ImageUpdaterService;

    @Autowired
    private OpenGraphUpdaterService openGraphUpdaterService;

    @Cacheable("statisticsCache")
    public BoqueiraoStatistics getStatistics() {
        log.info("Cache miss on statisticsCache. Fetching information from AESA and recalculating Statistics...");

        log.info("Step 1 - Getting statistics..");
        BoqueiraoStatistics statistics = statisticsService.getStatistics();

        log.info("Step 2 - Generating new image and saving to S3..");
        boolean imageGenerated = s3ImageUpdaterService.generateImageAndUpdate(statistics.getDaysToHappiness());

        if (!imageGenerated) {
            log.warn("Image generation failed! Won't attempt to refresh the Facebook Open Graph cache!");
            return statistics;
        }

        // This needs to be async otherwise Facebook will hit us before we save the cache
        log.info("Step 3 - Triggering Facebook Open Graph cache refresh..");
        new SimpleAsyncTaskExecutor().execute(openGraphUpdaterService::triggerFbCacheRefresh);

        return statistics;
    }
}
