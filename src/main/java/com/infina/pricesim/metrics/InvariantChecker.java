package com.infina.pricesim.metrics;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.infina.pricesim.service.CoinService;
import com.infina.pricesim.state.SafeCoinState;

@Component
public class InvariantChecker {

private final CoinService coinService;

public InvariantChecker(CoinService coinService) {
	this.coinService = coinService;
}

public boolean checkInvariant(Map<String, ExpectedCoinResult> expectedResults) {
	 return expectedResults.values()
			 .stream()
			 .allMatch(this::isCoinValid);
 }

 private boolean isCoinValid(ExpectedCoinResult expectedCoin) {
	 SafeCoinState safeCoin = coinService.getSafeCoin(expectedCoin.getCoinId());
	 return isPriceValid(expectedCoin, safeCoin) && isUpdateCountValid(expectedCoin, safeCoin);
 }
 
 private boolean isPriceValid(
		 ExpectedCoinResult expectedCoin,
		 SafeCoinState safeCoin
		 ) {
 
	 return expectedCoin.getExpectedPrice() == safeCoin.getCurrentPrice();
 }
	
 private boolean isUpdateCountValid(
		 ExpectedCoinResult expectedCoin,
		 SafeCoinState safeCoin
		 ) {
 
	 return expectedCoin.getExpectedUpdateCount() == safeCoin.getUpdateCount();
 }

}