package com.infina.pricesim.metrics;

import lombok.Builder;

@Builder
public record CoinComparison(
		String coinId,
		long initialPrice,
		
		long expectedPrice,
		long safePrice,
		long unsafePrice,
		
		long expectedUpdateCount,
		long safeUpdateCount,
		long unsafeUpdateCount
		) {

}
