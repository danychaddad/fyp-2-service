package com.forestfire.app.simulation;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forestfire.app.FireMonitoringService;
import com.forestfire.app.nodes.Node;
import com.forestfire.app.nodes.NodeRepository;
import com.forestfire.app.sensors.SensorReading;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FireSimulationService {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private FireMonitoringService fireMonitoringService;

    /**
     * Simulate a fire that occurs and is extinguished in a specific forest.
     */
    public void simulateFireExtinguishedInForest(String forestId) {
        List<Node> nodes = nodeRepository.findByForestId(forestId);
        if (nodes.isEmpty()) {
            log.warn("No nodes found in forest with ID: {}", forestId);
            return;
        }
        // Reset danger level for all nodes in the forest
        nodes.forEach(node -> {
            node.setDangerLevel(0);
            nodeRepository.save(node); // Persist changes
        });
        log.info("Reset danger levels for all nodes in forest {}", forestId);
        Node randomNode = nodes.get(new Random().nextInt(nodes.size()));
        log.info("Simulating fire in forest {} at node {}", forestId, randomNode.getMacAddress());
        simulateReading(randomNode, 80.0f, 70.0f, 90.0f, 1);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Extinguishing fire in forest {} at node {}", forestId, randomNode.getMacAddress());
        simulateReading(randomNode, 25.0f, 40.0f, 20.0f, 0);
    }

    /**
     * Simulate a fire that spreads within a specific forest.
     */
    public void simulateFireSpreadInForest(String forestId) {
        List<Node> nodes = nodeRepository.findByForestId(forestId);
        if (nodes.isEmpty()) {
            log.warn("No nodes found in forest with ID: {}", forestId);
            return;
        }

        // Reset danger level for all nodes in the forest
        nodes.forEach(node -> {
            node.setDangerLevel(0);
            nodeRepository.save(node); // Persist changes
        });
        log.info("Reset danger levels for all nodes in forest {}", forestId);

        // Select a random starting node
        Node startNode = nodes.get(new Random().nextInt(nodes.size()));
        log.info("Simulating fire in forest {} starting at node {}", forestId, startNode.getMacAddress());

        // BFS to spread fire
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Set<String> levelOneNodes = new HashSet<>();

        // Start fire at the random node
        simulateReading(startNode, 90.0f, 80.0f, 95.0f, 1);
        levelOneNodes.add(startNode.getMacAddress()); // Track level 1 nodes
        queue.add(startNode);
        visited.add(startNode.getMacAddress());

        while (!queue.isEmpty()) {
            // Resend abnormal readings for all level 1 nodes
            for (String macAddress : levelOneNodes) {
                Node node = nodeRepository.findByMacAddress(macAddress);
                if (node != null) {
                    log.info("Resending abnormal reading for node {}", node.getMacAddress());
                    simulateReading(node, 90.0f, 80.0f, 95.0f, 1); // Resend abnormal data
                }
            }

            Node currentNode = queue.poll();
            List<String> neighbors = currentNode.getNeighbors();

            for (String neighborId : neighbors) {
                Node neighbor = nodeRepository.findById(neighborId).orElse(null);

                // Ensure the neighbor is valid, belongs to the same forest, and hasn't been
                // visited
                if (neighbor != null && neighbor.getForestId().equals(forestId)
                        && !visited.contains(neighbor.getMacAddress())) {
                    try {
                        Thread.sleep(3000); // Spread after 3 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    log.info("Spreading fire in forest {} to neighbor node {}", forestId, neighbor.getMacAddress());
                    simulateReading(neighbor, 85.0f, 75.0f, 88.0f, 1);

                    // Mark as visited and add to queue
                    visited.add(neighbor.getMacAddress());
                    queue.add(neighbor);

                    // Add to level 1 nodes for resending abnormal readings
                    levelOneNodes.add(neighbor.getMacAddress());
                }
            }
        }
    }

    /**
     * Simulate a fire that spreads within a specific forest, then stops.
     */
    public void simulateFireExtinguishInForest(String forestId) {
        List<Node> nodes = nodeRepository.findByForestId(forestId);
        if (nodes.isEmpty()) {
            log.warn("No nodes found in forest with ID: {}", forestId);
            return;
        }

        // Reset danger level for all nodes in the forest
        nodes.forEach(node -> {
            node.setDangerLevel(0);
            nodeRepository.save(node); // Persist changes
        });
        log.info("Reset danger levels for all nodes in forest {}", forestId);

        // Select a random starting node
        Node startNode = nodes.get(new Random().nextInt(nodes.size()));
        log.info("Simulating fire in forest {} starting at node {}", forestId, startNode.getMacAddress());

        // BFS to spread fire
        Queue<Node> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Set<String> levelOneNodes = new HashSet<>();

        // Start fire at the random node
        simulateReading(startNode, 90.0f, 80.0f, 95.0f, 1);
        levelOneNodes.add(startNode.getMacAddress()); // Track level 1 nodes
        queue.add(startNode);
        visited.add(startNode.getMacAddress());

        int burnedNodes = 0;
        final int maxBurnedNodes = 15; // Stop fire after this many nodes are burned

        while (!queue.isEmpty()) {
            // Resend abnormal readings for all level 1 nodes
            for (String macAddress : levelOneNodes) {
                Node node = nodeRepository.findByMacAddress(macAddress);
                if (node != null) {
                    log.info("Resending abnormal reading for node {}", node.getMacAddress());
                    simulateReading(node, 90.0f, 80.0f, 95.0f, 1); // Resend abnormal data
                }
            }

            if (burnedNodes >= maxBurnedNodes) {
                log.info("Stopping fire simulation after {} nodes burned in forest {}", burnedNodes, forestId);
                break;
            }

            Node currentNode = queue.poll();
            List<String> neighbors = currentNode.getNeighbors();

            for (String neighborId : neighbors) {
                Node neighbor = nodeRepository.findById(neighborId).orElse(null);

                // Ensure the neighbor is valid, belongs to the same forest, and hasn't been
                // visited
                if (neighbor != null && neighbor.getForestId().equals(forestId)
                        && !visited.contains(neighbor.getMacAddress())) {
                    try {
                        Thread.sleep(3000); // Spread after 3 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    log.info("Spreading fire in forest {} to neighbor node {}", forestId, neighbor.getMacAddress());
                    simulateReading(neighbor, 85.0f, 75.0f, 88.0f, 1);

                    // Mark as visited and add to queue
                    visited.add(neighbor.getMacAddress());
                    queue.add(neighbor);

                    // Add to level 1 nodes for resending abnormal readings
                    levelOneNodes.add(neighbor.getMacAddress());
                    burnedNodes++;
                }
            }
        }

        // After the fire stops, reset all nodes to send good readings
        log.info("Resetting all nodes in forest {} to send good readings", forestId);
        nodes.forEach(node -> {
            simulateReading(node, 20.0f, 15.0f, 10.0f, 0); // Good readings
        });
    }

    /**
     * Generate a SensorReading and process it for a node.
     */
    private void simulateReading(Node node, float temperature, float humidity, float gasSensorReading,
            int cameraReading) {
        SensorReading reading = SensorReading.builder()
                .nodeId(node.getMacAddress())
                .temperature(temperature)
                .humidity(humidity)
                .gasSensorReading(gasSensorReading)
                .cameraReading(cameraReading)
                .timestamp(new Date())
                .build();

        fireMonitoringService.updateNodeDangerLevel(node.getMacAddress(), reading);
    }
}
