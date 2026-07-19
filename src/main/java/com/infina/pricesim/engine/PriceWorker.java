package com.infina.pricesim.engine;

import java.util.Objects;
import java.util.function.Consumer;

import com.infina.pricesim.counter.ICounter;
import com.infina.pricesim.model.PriceUpdateTask;

public class PriceWorker implements Runnable {

	private final TaskQueue taskQueue;
	private final ICounter counter;
	private final Consumer<PriceUpdateTask> taskConsumer;

	public PriceWorker(TaskQueue taskQueue, ICounter counter, Consumer<PriceUpdateTask> taskConsumer) {
	    this.taskQueue = Objects.requireNonNull(taskQueue, "taskQueue cannot be null");
	    this.counter = Objects.requireNonNull(counter, "counter cannot be null");
	    this.taskConsumer = Objects.requireNonNull(taskConsumer, "taskConsumer cannot be null");

	}

	@Override
	public void run() {
		processAvailableTasks();
	}
	
	private void processAvailableTasks() {
		PriceUpdateTask task;
		
		while((task = taskQueue.poll()) != null) {
			processTask(task);
		}
	}
	
	private void processTask(PriceUpdateTask task) {
		taskConsumer.accept(task);
		counter.increment();
		
		// Thread Dump Snapshot
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
