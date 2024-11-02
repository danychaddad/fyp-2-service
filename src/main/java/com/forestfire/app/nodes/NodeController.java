package com.forestfire.app.nodes;

import com.forestfire.app.sensors.SensorReadingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
