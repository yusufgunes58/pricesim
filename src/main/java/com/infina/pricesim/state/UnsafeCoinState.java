package com.infina.pricesim.state;

import com.infina.pricesim.model.PriceUpdateTask;

public class UnsafeCoinState extends CoinState {

	public UnsafeCoinState(String id, long initialPrice) {
		super(id, initialPrice);
	}

	@Override
	public void apply(PriceUpdateTask task) {
		updateState(task);
	}

}