package com.forestfire.app.sensors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.forestfire.app.FireMonitoringService;

import java.util.List;

@RestController
@Slf4j
public class SensorReadingController {
    private final SensorReadingRepository repo;

    @Autowired
    private FireMonitoringService fireMonitoringService;

    public SensorReadingController(SensorReadingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/readings")
    public List<SensorReading> all() {
        log.info("Fetching readings from the database");
        return repo.findAll();
    }

    @PostMapping("/nodes/{nodeId}/readings")
    public int newReading(@PathVariable("nodeId") String nodeId,
            @RequestBody SensorReading newReading) {
        SensorReading readingWithTimestamp = SensorReading.builder()
                .nodeId(nodeId)
                .temperature(newReading.getTemperature())
                .humidity(newReading.getHumidity())
                .gasSensorReading(newReading.getGasSensorReading())
                .cameraReading(newReading.getCameraReading())
                .timestamp(newReading.getTimestamp())
                .build();

        SensorReading inserted = repo.insert(readingWithTimestamp);
        log.info("Inserting new sensor reading in the database: {}", inserted);

        int dangerLevel = fireMonitoringService.updateNodeDangerLevel(nodeId, inserted);

        int refreshRate;
        switch (dangerLevel) {
            case 0:
                refreshRate = 60000; // 1 minute
                break;
            case 1:
                refreshRate = 30000; // 30 seconds
                break;
            case 2:
                refreshRate = 10000; // 10 seconds
                break;
            default:
                refreshRate = 60000; // Default to 1 minute
        }

        return refreshRate;
    }

    @GetMapping("/nodes/{nodeId}/readings")
    public ResponseEntity<List<SensorReading>> getReadingsByNodeId(@PathVariable("nodeId") String nodeId) {
        return ResponseEntity.ok(repo.findSensorReadingsByNodeId(nodeId));
    }
}
