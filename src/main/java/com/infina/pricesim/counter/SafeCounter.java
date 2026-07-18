package com.infina.pricesim.counter;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component("safeCounter")
public class SafeCounter implements ICounter {

	private final AtomicLong counter = new AtomicLong();  // used only one task 

	@Override
	public void increment() {
		counter.incrementAndGet();
	}

	@Override
	public long get() {
		return counter.get();
	}

	@Override
	public void reset() {
		counter.set(0);
	}
}
