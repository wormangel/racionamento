package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.StatisticsService;
import java.io.IOException;
import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @Autowired
    private StatisticsService statisticsService;

    @RequestMapping(value = "/")
    public String home(Model model) throws IOException, ParseException {
        BoqueiraoStatistics stats = statisticsService.getStatistics();
        model.addAttribute("data", stats);
        return "index";
    }
}
