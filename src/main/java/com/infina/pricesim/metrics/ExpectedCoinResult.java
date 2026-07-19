package com.infina.pricesim.metrics;

public class ExpectedCoinResult {

	private final String coinId;
	private final long initialPrice;

	private long expectedPrice;
	private int expectedUpdateCount;

	public ExpectedCoinResult(String coinId, long initialPrice) {
		this.coinId = coinId;
		this.initialPrice = initialPrice;
		this.expectedPrice = initialPrice;

	}

	public void applyUpdate(long priceUpdate) {
		this.expectedPrice += priceUpdate;
		this.expectedUpdateCount++;
	}
	
	public String getCoinId() {
		return coinId;
	}
	
	public long getInitialPrice() {
		return initialPrice;
	}
	
	public long getExpectedPrice() {
		return expectedPrice;
	}
	
	public int getExpectedUpdateCount() {
		return expectedUpdateCount;
	}
	
	
}
