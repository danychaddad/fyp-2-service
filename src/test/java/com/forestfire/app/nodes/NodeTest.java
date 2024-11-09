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

    @Test
    @DisplayName("addSensorReading() should correctly add a sensor reading to the list of sensor readings")
    void add_sensor_reading_should_correctly_add_a_sensor_reading_to_the_list_of_sensor_readings() {
        SensorReading reading = SensorReading.builder().build();
        assertTrue(n.addSensorReadingId(reading.getId()));
        assertEquals(n.getSensorReadingIds().size(), 1);
    }

    @Test
    @DisplayName("addSensorReading() should add the correct sensor reading to the list")
    void add_sensor_reading_should_add_the_correct_sensor_reading_to_the_list() {
        SensorReading reading = SensorReading.builder()
                .gasSensorReading(1.2f)
                .humidity(1.5f)
                .temperature(35.2f).build();
        n.addSensorReadingId(reading.getId());
        assertEquals(reading.getId(),n.getSensorReadingIds().getFirst());
    }
}