package com.forestfire.app.nodes.requests;

import com.forestfire.app.sensors.SensorReading;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NodeSensorReadingRequest {
    private String nodeId;
    private SensorReading reading;
}
