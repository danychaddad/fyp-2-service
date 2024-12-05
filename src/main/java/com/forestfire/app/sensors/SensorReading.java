package com.forestfire.app.sensors;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("sensor_readings")
public class SensorReading {
    @Id
    private String id;
    private final String nodeId;
    private final float temperature;
    private final float humidity;
    private final float gasSensorReading;
    private final int cameraReading;
    private final Date timestamp;

    @Builder
    public SensorReading(String nodeId, float temperature, float humidity, float gasSensorReading, int cameraReading, Date timestamp) {
        this.nodeId = nodeId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.gasSensorReading = gasSensorReading;
        this.cameraReading = cameraReading;
        this.timestamp = timestamp;
    }
}