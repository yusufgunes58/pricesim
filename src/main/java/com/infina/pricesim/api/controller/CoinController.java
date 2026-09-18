package com.infina.pricesim.api.controller;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infina.pricesim.service.CoinService;
import com.infina.pricesim.state.SafeCoinState;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/coins")
@Tag(name="Coins",
description="About coins in details with stats ")
public class CoinController {

    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @Operation(
            summary = "Get safe coin states",
            description = "Returns the latest safe coin states."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coins returned successfully"),
            @ApiResponse(responseCode = "404", description = "No simulation result found")
    })
    @GetMapping
    public ResponseEntity<List<SafeCoinState>> getCoins() {
        return ResponseEntity.ok(coinService.getSafeCoins());
    }

    @PostMapping("/id/price")
    public ResponseEntity<String> addCoin(@RequestParam String id, @RequestParam long price) {
        coinService.addCoin(id, price);
        return ResponseEntity.ok("Coin added successfully, Id:"+id+"\tPrice: "+price);
    }
}