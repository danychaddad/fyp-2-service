package com.forestfire.app.nodes;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {
    @Id
    private String id;

    private String macAddress;
    private float longitude;
    private float latitude;
    private List<String> sensorReadingIds;

    @Builder
    public Node(float longitude, float latitude, String macAddress, String id) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.macAddress = macAddress;
        this.id = id;
        this.sensorReadingIds = new ArrayList<>();
    }


    public boolean addSensorReadingId(String readingId) {
        return sensorReadingIds.add(readingId);
    }
}
