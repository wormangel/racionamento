package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<BoqueiraoStatistics> statistics() throws IOException, ParseException {
        return new ResponseEntity<>(statisticsService.getStatistics(), HttpStatus.OK);
    }
}
