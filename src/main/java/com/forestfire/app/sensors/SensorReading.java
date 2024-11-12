package com.forestfire.app.sensors;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("sensor_readings")
public class SensorReading {
    @Id
    private String id;
    private final String nodeId;
    private final float temperature;
    private final float humidity;
    private final float gasSensorReading;

    @Builder
    public SensorReading(float temperature, float humidity, float gasSensorReading, String nodeId) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.gasSensorReading = gasSensorReading;
        this.nodeId = nodeId;
    }
}
