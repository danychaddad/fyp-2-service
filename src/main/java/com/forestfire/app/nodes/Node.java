package com.forestfire.app.nodes;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {
    @Id
    private String macAddress;
    private float longitude;
    private float latitude;

    @Builder
    public Node(float longitude, float latitude, String macAddress) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.macAddress = macAddress;
    }

    public String getId() {
        return macAddress;
    }
}
