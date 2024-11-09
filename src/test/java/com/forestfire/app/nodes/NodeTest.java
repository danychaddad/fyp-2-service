package com.forestfire.app.nodes;

import com.forestfire.app.sensors.SensorReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    Node n;

    @BeforeEach
    void setup() {
        n = Node.builder().macAddress("test-id").latitude(123f).longitude(123f).build();
    }
}