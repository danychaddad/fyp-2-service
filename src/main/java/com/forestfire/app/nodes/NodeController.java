package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;
import com.forestfire.app.nodes.requests.NodeSensorReadingRequest;
import com.forestfire.app.sensors.SensorReading;
import com.forestfire.app.sensors.SensorReadingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nodes")
@Slf4j
public class NodeController {

    private final NodeRepository nodeRepository;

    @Autowired
    public NodeController(NodeRepository nodeRepository, SensorReadingRepository sensorReadingRepository) {
        this.nodeRepository = nodeRepository;
    }

    @GetMapping
    public List<Node> all() {
        return nodeRepository.findAll();
    }

    // TODO: Add get neighbors logic
    @PostMapping("/hello")
    public ResponseEntity<Node> hello(@RequestBody NodeHelloRequest req) {
        Optional<Node> existingNode = nodeRepository.findById(req.getMacAddress());

        if (existingNode.isPresent()) {
            log.info("Node with MAC: {} already exists, returning existing node.", req.getMacAddress());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(existingNode.get());
        }

        log.info("Adding node with MAC: {}, longitude: {}, latitude: {}, to the list",
                req.getMacAddress(), req.getLongitude(), req.getLatitude());

        Node newNode = Node.builder()
                .macAddress(req.getMacAddress())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();
        nodeRepository.save(newNode);

        return ResponseEntity.status(HttpStatus.CREATED).body(newNode);
    }

    @GetMapping("/{nodeId}")
    public ResponseEntity<Node> getSensorById(@PathVariable("nodeId") String nodeId) {
        Optional<Node> node = nodeRepository.findById(nodeId);
        return node.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}