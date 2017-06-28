package com.github.wormangel.racionamento.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        // Needed because AESA doesn't return a Content-Type header and then RestTemplate would assume application/octet-stream
        // We then say 'hey, use Jackson for that as well'
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(
                Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM}));

        restTemplate.setMessageConverters(Arrays.asList(converter, new FormHttpMessageConverter()));
        return restTemplate;
    }
}
