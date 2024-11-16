package com.forestfire.app.nodes;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Node {
    @Id
    private String macAddress;
    
    private float longitude;
    private float latitude;
    private String forestId;

    @Builder
    public Node(float longitude, float latitude, String macAddress, String forestId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.macAddress = macAddress;
        this.forestId = forestId;
    }

    public String getId() {
        return macAddress;
    }
}
