package com.forestfire.app.sensors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class SensorReadingController {
    private final SensorReadingRepository repo;

    public SensorReadingController(SensorReadingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/readings")
    public List<SensorReading> all() {
        log.info("Fetching readings from the database");
        return repo.findAll();
    }

    @PostMapping("/readings")
    public SensorReading newReading(@RequestBody SensorReading newReading) {
        SensorReading inserted = repo.insert(newReading);
        log.info("Inserting new sensor reading in the database: {}", inserted);
        return inserted;
    }
}
