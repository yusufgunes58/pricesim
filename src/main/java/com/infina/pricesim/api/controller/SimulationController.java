package com.infina.pricesim.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.infina.pricesim.service.SimCoordinator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/simulate")
@Validated
@Tag(name="Simulation",
	 description="Simulation managed")		
public class SimulationController {

    private final SimCoordinator simCoordinator;

    public SimulationController(SimCoordinator simCoordinator) {
        this.simCoordinator = simCoordinator;
    }
    @Operation(summary = "Start simulation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulation completed"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "409", description = "Simulation already running")
    })
    @PostMapping
    public ResponseEntity<Void> simulate(

            @Parameter(description = "Update count")
            @RequestParam
            @Min(value = 1, message = "updates must be at least 1")
            @Max(value = 100000, message = "updates must not exceed 100000")
            int updates,

            @Parameter(description = "Worker count")
            @RequestParam
            @Min(value = 1, message = "workers must be at least 1")
            @Max(value = 16, message = "workers must not exceed 16")
            int workers,

            @Parameter(description = "Random seed")
            @RequestParam(required = false)
            Long seed

    ) throws InterruptedException {

        simCoordinator.simulate(updates, workers, seed);

        return ResponseEntity.ok().build();
    }
}