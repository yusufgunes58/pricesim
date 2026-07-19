package com.infina.pricesim.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.infina.pricesim.service.SimCoordinator;




@RestController
@RequestMapping("/simulate")
public class SimulationController {

    private final SimCoordinator simCoordinator;

    public SimulationController(SimCoordinator simCoordinator) {
        this.simCoordinator = simCoordinator;
    }

    @PostMapping
    public ResponseEntity<Void> simulate(
            @RequestParam int updates,
            @RequestParam int workers,
            @RequestParam(required = false) Long seed)
            throws InterruptedException {

        simCoordinator.simulate(updates, workers, seed);

        return ResponseEntity.ok().build();
    }
}