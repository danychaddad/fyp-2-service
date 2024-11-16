package com.forestfire.app.nodes;

import org.junit.jupiter.api.BeforeEach;

class NodeTest {
    Node n;

    @BeforeEach
    void setup() {
        n = Node.builder().macAddress("test-id").latitude(123f).longitude(123f).forestId("forest-id").build();
    }
}