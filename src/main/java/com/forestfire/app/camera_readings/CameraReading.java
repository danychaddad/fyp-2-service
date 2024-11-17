package com.forestfire.app.camera_readings;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Document("camera_readings")
public class CameraReading {
    @Id
    private String id;
    private final String nodeId;
    private final String image;
    private final Date timestamp;

    @Builder
    public CameraReading(String nodeId, String image, Date timestamp) {
        this.nodeId = nodeId;
        this.image = image;
        this.timestamp = timestamp;
    }
}