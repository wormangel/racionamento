package com.github.wormangel.racionamento.controller;

import com.github.wormangel.racionamento.model.BoqueiraoStatistics;
import com.github.wormangel.racionamento.service.BoqueiraoService;
import java.io.IOException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class WebController {
    @Autowired
    private BoqueiraoService boqueiraoService;

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "/")
    public String home(@RequestParam(name = "kill", required = false) String killCache, Model model) throws IOException, ParseException {
        if (killCache != null) {
            log.info("Page requested with cache kill parameter! Killing it...");
            cacheManager.getCache("statisticsCache").clear();
        }

        BoqueiraoStatistics stats = boqueiraoService.getStatistics();
        model.addAttribute("data", stats);
        return "index";
    }
}
