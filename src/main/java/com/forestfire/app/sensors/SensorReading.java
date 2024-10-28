package com.forestfire.app.sensors;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("sensor_readings")
public class SensorReading {
    @Id
    private String id;

    private float temperature;
    private float humidity;
    private float gasSensorReading;
}
