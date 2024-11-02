package com.forestfire.app.nodes;

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
    private List<String> sensorReadingIds = new ArrayList<>();

    public boolean addSensorReadingId(String readingId) {
        return sensorReadingIds.add(readingId);
    }
}
