package me.demo.logging_Activity.controller;

import me.demo.logging_Activity.utils.CsvReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logging")
public class LoggingController {
    private static final Logger logger = LogManager.getLogger(LoggingController.class);

    @Autowired
    private CsvReader csvReader;

    @GetMapping("display")
    public String displayLogs() {
        logger.info("User clicked!");
        csvReader.display();
        return "display logs";
    }
}
