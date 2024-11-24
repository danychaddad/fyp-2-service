package com.forestfire.app;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.forestfire.app.nodes.Node;
import com.forestfire.app.nodes.NodeRepository;
import com.forestfire.app.sensors.SensorReading;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FireMonitoringService {
    private static final float TEMPERATURE_THRESHOLD = 50.0f;  // Celsius
    private static final float GAS_THRESHOLD = 100.0f;         // Arbitrary units
    private static final long OFFLINE_THRESHOLD = 300000;      // 5 minutes in milliseconds

    @Autowired
    private NodeRepository nodeRepository;

    public boolean isReadingDangerous(SensorReading reading) {
        return reading.getTemperature() > TEMPERATURE_THRESHOLD ||
               reading.getGasSensorReading() > GAS_THRESHOLD;
    }

    public void updateNodeDangerLevel(String nodeId, SensorReading reading) {
        Node node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));
        
        boolean isAbnormalReading = isReadingDangerous(reading);
        int currentLevel = node.getDangerLevel();
        int newDangerLevel = currentLevel;
        
        // Get all neighbor nodes
        List<Node> neighbors = node.getNeighbors().stream()
                .map(neighborId -> nodeRepository.findById(neighborId)
                        .orElseThrow(() -> new RuntimeException("Neighbor node not found: " + neighborId)))
                .collect(Collectors.toList());
        
        if (isAbnormalReading) {
            // Case: Level 0 node with Abnormal reading and all neighbors level 0 -> Level 1
            if (currentLevel == 0 && hasNeighborAtLevel(neighbors, 0)) {
                newDangerLevel = 1;
                log.warn("Node {} escalated to danger level 1 (initial fire detection)", nodeId);
            }
            // Case: Level 0/1 node with Abnormal reading and all neighbors level 1 or 2 -> Level 2
            else if ((currentLevel == 0 || currentLevel == 1) && 
                    areAllNeighborsAtLevelOrHigher(neighbors, 1)) {
                newDangerLevel = 2;
                log.warn("Node {} escalated to danger level 2 (fire spreading)", nodeId);
            }
            // Case: Level 2 node with Abnormal reading and has level 0 neighbor -> Level 1
            else if (currentLevel == 2 && hasNeighborAtLevel(neighbors, 0)) {
                newDangerLevel = 1;
                log.warn("Node {} reduced to danger level 1 (fire containment)", nodeId);
            }
        } else {
            // Case: Level 1 node returns to normal -> Level 0
            if (currentLevel == 1) {
                newDangerLevel = 0;
                log.info("Node {} returned to normal (level 0)", nodeId);
            }
        }
        
        // Update node if danger level changed
        if (currentLevel != newDangerLevel) {
            node.setDangerLevel(newDangerLevel);
            nodeRepository.save(node);
        }
        
        // Always update last reading timestamp
        node.setLastReading(reading.getTimestamp());
        nodeRepository.save(node);
    }

    private boolean areAllNeighborsAtLevel(List<Node> neighbors, int level) {
        return !neighbors.isEmpty() && 
               neighbors.stream().allMatch(n -> n.getDangerLevel() == level);
    }

    private boolean areAllNeighborsAtLevelOrHigher(List<Node> neighbors, int level) {
        return !neighbors.isEmpty() && 
               neighbors.stream().allMatch(n -> n.getDangerLevel() >= level);
    }

    private boolean hasNeighborAtLevel(List<Node> neighbors, int level) {
        return neighbors.stream().anyMatch(n -> n.getDangerLevel() == level);
    }

    @Scheduled(fixedRate = 60000)
    public void checkOfflineNodes() {
        Date threshold = new Date(System.currentTimeMillis() - OFFLINE_THRESHOLD);
        List<Node> nodes = nodeRepository.findByLastReadingBefore(threshold);
        
        for (Node node : nodes) {
            if (node.getDangerLevel() == 0) {
                node.setDangerLevel(1);
                log.warn("Node {} marked as danger level 1 due to being offline", node.getId());
                nodeRepository.save(node);
            }
        }
    }
}