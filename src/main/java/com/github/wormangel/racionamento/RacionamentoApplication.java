package com.github.wormangel.racionamento;

import com.github.wormangel.racionamento.service.BoqueiraoService;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@Slf4j
public class RacionamentoApplication {
    @Autowired
    private BoqueiraoService boqueiraoService;

    public static void main(String[] args) {
        SpringApplication.run(RacionamentoApplication.class, args);
    }

	@Scheduled(cron = "0 15 10 ? * *", zone = "America/Recife")
	public void triggerStatisticsUpdate() throws IOException, ParseException {
		log.info("Good morning! Scheduled task to update the statistics starting...");
		new SimpleAsyncTaskExecutor().execute(this::update);
	}

	@CacheEvict("statisticsCache")
	public void update() {
		try {
			boqueiraoService.getStatistics();
		} catch (Exception e) {
			log.error("Error updating statistics asynchronously!");
		}
	}
}
