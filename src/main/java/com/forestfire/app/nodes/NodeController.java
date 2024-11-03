package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;
import com.forestfire.app.sensors.SensorReadingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")
@Slf4j
public class NodeController {

    private final NodeRepository nodeRepository;
    private final SensorReadingRepository sensorReadingRepository;

    @Autowired
    public NodeController(NodeRepository nodeRepository, SensorReadingRepository sensorReadingRepository) {
        this.nodeRepository = nodeRepository;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @GetMapping("/all")
    public List<Node> all() {
        return nodeRepository.findAll();
    }

    @PostMapping("/hello")
    public ResponseEntity<Node> hello(@RequestBody String macAddress) {
        log.info("Adding node with MAC: {} to the list", macAddress);
        Node n = Node.builder().macAddress(macAddress).build();
        nodeRepository.save(n);
        return ResponseEntity.status(HttpStatus.CREATED).body(n);
    }
}
