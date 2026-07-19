package com.infina.pricesim.counter;

import org.springframework.stereotype.Component;

@Component("unsafeCounter")
public class UnsafeCounter implements ICounter {
	private long counter;

	@Override
	public void increment() {
		counter++;
	}

	@Override
	public long get() {
		return counter;
	}

	@Override
	public void reset() {
		counter = 0;
	}
}