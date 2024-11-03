package com.forestfire.app.nodes.requests;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class NodeHelloRequest implements Serializable {
    private String macAddress;
}
