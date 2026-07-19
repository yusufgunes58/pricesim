package com.infina.pricesim.state;

import java.util.concurrent.locks.ReentrantLock;

import com.infina.pricesim.model.PriceUpdateTask;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "Match UnsafeCoin info -> give this state some info")
public class SafeCoinState extends CoinState {

	private final ReentrantLock lock = new ReentrantLock();

	public SafeCoinState(String id, long initialPrice) {
		super(id, initialPrice);
	}

	@Override
	public void apply(PriceUpdateTask task) {
		lock.lock();
		try {
			updateState(task);
		} finally {
			lock.unlock();
		}
	}

}
