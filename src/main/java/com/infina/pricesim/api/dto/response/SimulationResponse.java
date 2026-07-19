package com.infina.pricesim.api.dto.response;

import java.util.List;

import com.infina.pricesim.metrics.CoinComparison;

import lombok.Builder;

@Builder
public record SimulationResponse(
		long seed,
		int submittedUpdates,
		
		long unsafeProcessedUpdates,
		long safeProcessedUpdates,
		
		int workers,
		
		long unsafeElapsedMs,
		long safeElapsedMs,
		
		long unsafeThroughputPerSec,
		long safeThroughputPerSec,
		
		
		boolean safeInvariantPassed,
		
		List<CoinComparison> coinComparisons
		
		) {

}
