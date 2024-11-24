package com.forestfire.app.nodes;

import com.forestfire.app.nodes.requests.NodeHelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class NodeController {

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
                .build();

        nodeRepository.save(newNode);

        updateNeighbors(newNode);

        return ResponseEntity.status(HttpStatus.CREATED).body(newNode);
    }

    private void updateNeighbors(Node newNode) {
        List<Node> forestNodes = nodeRepository.findByForestId(newNode.getForestId());

        for (Node node : forestNodes) {
            if (!node.getMacAddress().equals(newNode.getMacAddress())) {
                double distance = calculateDistance(newNode.getLatitude(), newNode.getLongitude(),
                        node.getLatitude(), node.getLongitude());
                if (distance <= 500) {
                    addNeighbor(node, newNode);
                    addNeighbor(newNode, node);
                }
            }
        }
    }

    private void addNeighbor(Node node, Node neighbor) {
        if (node.getNeighbors().size() < 3 && !node.getNeighbors().contains(neighbor.getMacAddress())) {
            node.getNeighbors().add(neighbor.getMacAddress());
            nodeRepository.save(node);
        }
    }

    private double calculateDistance(float lat1, float lon1, float lat2, float lon2) {
        final int EARTH_RADIUS = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
