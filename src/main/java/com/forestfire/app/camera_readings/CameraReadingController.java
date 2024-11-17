package com.forestfire.app.camera_readings;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/nodes/{nodeId}/images")
public class CameraReadingController {
    private final CameraReadingRepository repo;

    public CameraReadingController(CameraReadingRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<List<CameraReading>> getImagesByNodeId(@PathVariable("nodeId") String nodeId) {
        log.info("Fetching all images for node: {}", nodeId);
        List<CameraReading> readings = repo.findCameraReadingsByNodeId(nodeId);
        return ResponseEntity.ok(readings);
    }

    @PostMapping
    public ResponseEntity<CameraReading> newImage(
            @PathVariable("nodeId") String nodeId,
            @RequestBody CameraReading newReading) {
        CameraReading readingWithTimestamp = CameraReading.builder()
                .nodeId(nodeId)
                .image(newReading.getImage())
                .timestamp(new Date())
                .build();
        
        CameraReading inserted = repo.insert(readingWithTimestamp);
        log.info("Inserting new camera reading in the database: {}", inserted);
        return ResponseEntity.ok(inserted);
    }

    @GetMapping("/latest")
    public ResponseEntity<CameraReading> getLastImageForNodeId(@PathVariable("nodeId") String nodeId) {
        log.info("Fetching latest image for node: {}", nodeId);
        CameraReading latestReading = repo.findTopByNodeIdOrderByTimestampDesc(nodeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No images found for node: " + nodeId));
        return ResponseEntity.ok(latestReading);
    }
}