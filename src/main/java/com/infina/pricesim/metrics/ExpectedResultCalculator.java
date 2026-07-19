package com.infina.pricesim.metrics;

import com.infina.pricesim.model.PriceUpdateTask;
import com.infina.pricesim.service.CoinService;
import com.infina.pricesim.state.SafeCoinState;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExpectedResultCalculator {

    private final CoinService coinService;

    public ExpectedResultCalculator(CoinService coinService) {
        this.coinService = coinService;
    }

    public Map<String, ExpectedCoinResult> calculate(List<PriceUpdateTask> tasks) {

        Map<String, ExpectedCoinResult> expectedResults = initializeExpectedResults();

        processTasks(tasks, expectedResults);

        return expectedResults;
    }

    private Map<String, ExpectedCoinResult> initializeExpectedResults() {

        Map<String, ExpectedCoinResult> expectedResults = new LinkedHashMap<>();

        for (SafeCoinState coin : coinService.getSafeCoins()) {
            expectedResults.put(
                    coin.getId(),
                    new ExpectedCoinResult(
                            coin.getId(),
                            coin.getInitialPrice()
                    )
            );
        }

        return expectedResults;
    }

    private void processTasks(
            List<PriceUpdateTask> tasks,
            Map<String, ExpectedCoinResult> expectedResults) {

        for (PriceUpdateTask task : tasks) {
            applyTask(task, expectedResults);
        }
    }

    private void applyTask(
            PriceUpdateTask task,
            Map<String, ExpectedCoinResult> expectedResults) {

        ExpectedCoinResult expectedCoin =
                expectedResults.get(task.coinId());

        if (expectedCoin == null) {
            throw new IllegalStateException(
                    "Unknown coin id: " + task.coinId());
        }

        expectedCoin.applyUpdate(task.delta());
    }

}