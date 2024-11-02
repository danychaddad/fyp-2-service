package com.forestfire.app.nodes;

import com.forestfire.app.sensors.SensorReading;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Node {
    @Id
    private String id;

    private float longitude;
    private float latitude;
    @Builder.Default
    private List<SensorReading> sensorReadings = new ArrayList<>();

    public boolean addSensorReading(SensorReading reading) {
        return sensorReadings.add(reading);
    }
}
