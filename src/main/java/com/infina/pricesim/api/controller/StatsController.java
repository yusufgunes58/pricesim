package com.infina.pricesim.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infina.pricesim.api.dto.response.SimulationResponse;
import com.infina.pricesim.service.SimulationResultStore;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/stats")
@Tag(name="Stats of simulation",
description="Detailed stats and shows coins.")
public class StatsController {

    private final SimulationResultStore simulationResultStore;

    public StatsController(SimulationResultStore simulationResultStore) {
        this.simulationResultStore = simulationResultStore;
    }

    @Operation(
            summary = "Get simulation statistics",
            description = "Returns the latest simulation result."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics returned successfully"),
            @ApiResponse(responseCode = "404", description = "No simulation result found")
    })
    @GetMapping
    public ResponseEntity<SimulationResponse> getStats() {

        return ResponseEntity.ok(simulationResultStore.getLatest());
    }
}