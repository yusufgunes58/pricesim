package com.infina.pricesim.engine;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class SimulationLock {

	private final AtomicBoolean running = new AtomicBoolean(false);

	public boolean tryLock() {
		return running.compareAndSet(false, true);
	}

	public void unLock() {
		running.set(false);
	}

	public boolean isRunning() {
		return running.get();
	}
}
