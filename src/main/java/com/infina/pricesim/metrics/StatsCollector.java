package com.infina.pricesim.metrics;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.infina.pricesim.api.dto.response.SimulationResponse;
import com.infina.pricesim.counter.ICounter;
import com.infina.pricesim.service.CoinService;
import com.infina.pricesim.state.SafeCoinState;
import com.infina.pricesim.state.UnsafeCoinState;

@Component
public class StatsCollector {

	private static final int MILLISECONDS_IN_SECOND = 1000;

	private final CoinService coinService;
	private final ICounter safeCounter;
	private final ICounter unsafeCounter;
	private final InvariantChecker invariantChecker;

	public StatsCollector(
			CoinService coinService,
			@Qualifier("safeCounter") ICounter safeCounter,
			@Qualifier("unsafeCounter") ICounter unsafeCounter,
			InvariantChecker invariantChecker
			) {

		this.coinService = coinService;
		this.safeCounter = safeCounter;
		this.unsafeCounter = unsafeCounter;
		this.invariantChecker = invariantChecker;
	}

	public SimulationResponse collect(
			long seed,
			int submittedUpdates,
			int workers,
			long safeElapsedMs,
			long unsafeElapsedMs,
			Map<String, ExpectedCoinResult> expectedResults
			) {
	
		return SimulationResponse.builder()
				.seed(seed)
				.submittedUpdates(submittedUpdates)
				.safeProcessedUpdates(safeCounter.get())
				.unsafeProcessedUpdates(unsafeCounter.get())
				.workers(workers)
				.safeElapsedMs(safeElapsedMs)
				.unsafeElapsedMs(unsafeElapsedMs)
				.safeThroughputPerSec(calculateThroughput(safeCounter.get(), safeElapsedMs))
				.unsafeThroughputPerSec(calculateThroughput(unsafeCounter.get(), unsafeElapsedMs))
				.safeInvariantPassed(invariantChecker.checkInvariant(expectedResults))
				.coinComparisons(buildCoinComparisons(expectedResults))
				.build();
	}

	private CoinComparison createCoinComparison(ExpectedCoinResult expectedCoin) {

	    SafeCoinState safeCoin = coinService.getSafeCoin(expectedCoin.getCoinId());
	    UnsafeCoinState unsafeCoin = coinService.getUnsafeCoin(expectedCoin.getCoinId());

	    String coinId = expectedCoin.getCoinId();
	    long initialPrice = expectedCoin.getInitialPrice();
	    
	    long expectedPrice = expectedCoin.getExpectedPrice();
	    long safeCurrentPrice = safeCoin.getCurrentPrice();
	    long unsafeCurrentPrice = unsafeCoin.getCurrentPrice();

	    long expectedUpdates = expectedCoin.getExpectedUpdateCount();
	    long safeUpdates = safeCoin.getUpdateCount();
	    long unsafeUpdates = unsafeCoin.getUpdateCount();

	    return CoinComparison.builder()
	            .coinId(coinId)
	            .initialPrice(initialPrice)
	            .expectedPrice(expectedPrice)
	            .safePrice(safeCurrentPrice)
	            .unsafePrice(unsafeCurrentPrice)
	            .expectedUpdateCount(expectedUpdates)
	            .safeUpdateCount(safeUpdates)
	            .unsafeUpdateCount(unsafeUpdates)
	            .build();
	}

	private List<CoinComparison> buildCoinComparisons(Map<String, ExpectedCoinResult> expectedResults) {
	    return expectedResults.values().stream()
	            .map(this::createCoinComparison)
	            .toList();
	}

	private long calculateThroughput(long processedUpdates, long elapsedMs) {

		if (elapsedMs == 0) {
			return 0;
		}
		return processedUpdates * MILLISECONDS_IN_SECOND / elapsedMs;
	}
	
}


