package com.forestfire.app.nodes;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

@Data
public class Node {
    @Id
    private String macAddress;

    private float longitude;
    private float latitude;
    private String forestId;

    private List<String> neighbors = new ArrayList<>();

    @Builder
    public Node(float longitude, float latitude, String macAddress, String forestId, List<String> neighbors) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.macAddress = macAddress;
        this.forestId = forestId;
        this.neighbors = neighbors != null ? neighbors : new ArrayList<>();
    }

    public String getId() {
        return macAddress;
    }
}
