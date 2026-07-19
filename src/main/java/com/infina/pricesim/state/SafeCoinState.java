package com.infina.pricesim.state;

import java.util.concurrent.locks.ReentrantLock;

import com.infina.pricesim.model.PriceUpdateTask;

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
