package com.forestfire.app.sensors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @PostMapping("/nodes/{nodeId}/readings")
    public SensorReading newReading(@PathVariable("nodeId") String nodeId, @RequestBody SensorReading newReading) {
        SensorReading readingWithTimestamp = SensorReading.builder()
                .nodeId(nodeId)
                .temperature(newReading.getTemperature())
                .humidity(newReading.getHumidity())
                .gasSensorReading(newReading.getGasSensorReading())
                .timestamp(newReading.getTimestamp())
                .build();
        SensorReading inserted = repo.insert(readingWithTimestamp);
        log.info("Inserting new sensor reading in the database: {}", inserted);
        return inserted;
    }

    @GetMapping("/nodes/{nodeId}/readings")
    public ResponseEntity<List<SensorReading>> getReadingsByNodeId(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(repo.findSensorReadingsByNodeId(nodeId));
    }
}
