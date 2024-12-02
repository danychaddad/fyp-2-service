package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class NodeController {
    private static final double EARTH_RADIUS = 6371000;
    private static final double MAX_DISTANCE = 1000;
    private static final int MAX_NEIGHBORS = 3;

    private final NodeRepository nodeRepository;

    public NodeController(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @GetMapping("/nodes")
    public List<Node> all() {
        return nodeRepository.findAll();
    }

    @GetMapping("/nodes/{nodeId}")
    public ResponseEntity<Node> getSensorById(@PathVariable("nodeId") String nodeId) {
        Optional<Node> node = nodeRepository.findById(nodeId);
        return node.map(value -> ResponseEntity.status(HttpStatus.OK).body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/forests/{forestId}/nodes")
    public ResponseEntity<List<Node>> getNodesByForestId(@PathVariable("forestId") String forestId) {
        List<Node> nodes = nodeRepository.findByForestId(forestId);
        if (nodes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(nodes);
    }

    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<String> deleteNode(@PathVariable String id) {
        nodeRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted node with id: " + id);
    }

    @PostMapping("/nodes/hello")
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
                .forestId(req.getForestId())
                .neighbors(new ArrayList<>())
                .build();

        nodeRepository.save(newNode);

        updateNeighbors(newNode);

        Node updatedNode = nodeRepository.findById(newNode.getMacAddress())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created node"));

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedNode);
    }

    protected void updateNeighbors(Node newNode) {
        List<Node> forestNodes = nodeRepository.findByForestId(newNode.getForestId());
        Map<String, Map<String, Double>> distanceMap = new HashMap<>();

        for (Node node1 : forestNodes) {
            Map<String, Double> nodeDistances = new HashMap<>();
            distanceMap.put(node1.getMacAddress(), nodeDistances);
            for (Node node2 : forestNodes) {
                if (!node1.getMacAddress().equals(node2.getMacAddress())) {
                    double distance = calculateDistance(
                            node1.getLatitude(), node1.getLongitude(),
                            node2.getLatitude(), node2.getLongitude());
                    if (distance <= MAX_DISTANCE) {
                        nodeDistances.put(node2.getMacAddress(), distance);
                    }
                }
            }
        }

        for (Node node : forestNodes) {
            Map<String, Double> nodeDistances = distanceMap.get(node.getMacAddress());
            List<Map.Entry<String, Double>> sortedNeighbors = nodeDistances.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(MAX_NEIGHBORS)
                    .collect(Collectors.toList());

            List<String> newNeighbors = sortedNeighbors.stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            node.setNeighbors(newNeighbors);
            nodeRepository.save(node);
            log.info("Updated neighbors for node {}: {}", node.getMacAddress(), newNeighbors);
        }
    }

    private double calculateDistance(float lat1, float lon1, float lat2, float lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}