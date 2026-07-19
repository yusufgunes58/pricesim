package com.infina.pricesim.engine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Objects;

import com.infina.pricesim.model.PriceUpdateTask;

public class TaskQueue {

	private final BlockingQueue<PriceUpdateTask> queue;

	public TaskQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Queue capacity must be greater than zero!");
		}
		this.queue = new ArrayBlockingQueue<>(capacity);
	}

	public void enqueue(PriceUpdateTask task) {
		Objects.requireNonNull(task, "Task cannot be null");
		
		if(!queue.offer(task)) {
			throw new IllegalStateException("Queue is full.");
		}
	}

	public int size() {
		return queue.size();
	}

	public PriceUpdateTask poll() {
		return queue.poll();
	}
}
