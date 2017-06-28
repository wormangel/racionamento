package com.github.wormangel.racionamento.service;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class OpenGraphUpdaterService {

    public boolean triggerFbCacheRefresh() {
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
            return false;
        }

        return true;
    }

}
