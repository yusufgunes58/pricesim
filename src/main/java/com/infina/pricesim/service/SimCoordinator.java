package com.infina.pricesim.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.infina.pricesim.counter.ICounter;
import com.infina.pricesim.engine.PriceWorker;
import com.infina.pricesim.engine.TaskGenerator;
import com.infina.pricesim.engine.TaskQueue;
import com.infina.pricesim.model.PriceUpdateTask;

@Service
public class SimCoordinator {

	private final CoinService coinService;
	private final TaskGenerator taskGenerator;
	private final ICounter safeCounter;
	private final ICounter unsafeCounter;
	
	public SimCoordinator(
			CoinService coinService,
			TaskGenerator taskGenerator,
			@Qualifier("safeCounter") ICounter safeCounter,
		    @Qualifier("UnsafeCounter") ICounter unsafeCounter
			) {
		super();
		this.coinService = coinService;
		this.taskGenerator = taskGenerator;
		this.safeCounter = safeCounter;
		this.unsafeCounter = unsafeCounter;
	}
	
	public void simulate(int updates, int workers, Long seed) throws InterruptedException {
		
		this.coinService.reset();
		this.safeCounter.reset();
		this.unsafeCounter.reset();
		
		List<PriceUpdateTask> tasks = taskGenerator.generateTasks(updates, resolve(seed));
	
		executeSafeSimulation(tasks, workers);
		
		executeUnsafeSimulation(tasks, workers);
	
	}
	
	private void executeSafeSimulation(
			List<PriceUpdateTask> tasks,
			int workers) throws InterruptedException {
	
		executeSimulation(tasks, workers, safeCounter,
				task -> coinService.getSafeCoin(task.coinId()).apply(task));
	
	}
	
	private void executeUnsafeSimulation(
			List<PriceUpdateTask> tasks,
			int workers) throws InterruptedException {
	
		executeSimulation(tasks, workers, unsafeCounter,
				task -> coinService.getUnsafeCoin(task.coinId()).apply(task));
	
	}
	
	private void executeSimulation(
			List<PriceUpdateTask> tasks,
			int workers,
			ICounter counter,
			Consumer<PriceUpdateTask> taskExecutor)
			throws InterruptedException
	{
		
		TaskQueue taskQueue = createTaskQueue(tasks);
		
		ExecutorService executor = Executors.newFixedThreadPool(workers);
		
		for(int i=0; i<workers; i++) {
			executor.submit(new PriceWorker(taskQueue, counter, taskExecutor));
		}
	
		awaitCompletion(executor);
			
	}
	
	private TaskQueue createTaskQueue(List<PriceUpdateTask> tasks) throws InterruptedException {
		TaskQueue taskQueue = new TaskQueue(tasks.size());
		
		for(PriceUpdateTask task : tasks) {
			taskQueue.enqueue(task);
		}
		
		return taskQueue;
	}
	
	private void awaitCompletion(ExecutorService executor) {
		
		executor.shutdown();
		
		try {
			if(!executor.awaitTermination(1, TimeUnit.MINUTES)) {
				executor.shutdownNow();
				throw new IllegalStateException("Simulation did not complete in time.");
			}
			
		} catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
			
			throw new IllegalStateException("Simulation was interrupted.", e);
		}
	}
	
	private long resolve(Long seed) {
		return seed != null ? seed : System.currentTimeMillis();
	}
	
}