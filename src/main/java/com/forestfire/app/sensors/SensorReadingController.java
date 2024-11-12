package com.forestfire.app.sensors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readings")
@Slf4j
public class SensorReadingController {
    private final SensorReadingRepository repo;

    public SensorReadingController(SensorReadingRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<SensorReading> all() {
        log.info("Fetching readings from the database");
        return repo.findAll();
    }

    @PostMapping
    public SensorReading newReading(@RequestBody SensorReading newReading) {
        SensorReading inserted = repo.insert(newReading);
        log.info("Inserting new sensor reading in the database: {}", inserted);
        return inserted;
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<List<SensorReading>> getReadingsByNodeId(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(repo.findSensorReadingsByNodeId(nodeId));
    }
}
