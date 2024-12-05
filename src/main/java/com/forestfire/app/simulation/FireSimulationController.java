package com.forestfire.app.simulation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulate")
public class FireSimulationController {

    @Autowired
    private FireSimulationService fireSimulationService;

    @PostMapping("/small-fire/{forestId}")
    public void simulateFireExtinguished(@PathVariable String forestId) {
        fireSimulationService.simulateFireExtinguishedInForest(forestId);
    }

    @PostMapping("/fire-spread/{forestId}")
    public void simulateFireSpread(@PathVariable String forestId) {
        fireSimulationService.simulateFireSpreadInForest(forestId);
    }

    @PostMapping("/fire-extinguish/{forestId}")
    public void simulateFireExtinguish(@PathVariable String forestId) {
        fireSimulationService.simulateFireExtinguishInForest(forestId);
    }
}
