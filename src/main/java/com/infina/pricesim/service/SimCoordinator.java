package com.infina.pricesim.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.infina.pricesim.api.dto.response.SimulationResponse;
import com.infina.pricesim.api.exception.SimulationConflictException;
import com.infina.pricesim.counter.ICounter;
import com.infina.pricesim.engine.PriceWorker;
import com.infina.pricesim.engine.SimulationLock;
import com.infina.pricesim.engine.TaskGenerator;
import com.infina.pricesim.engine.TaskQueue;
import com.infina.pricesim.metrics.ExpectedCoinResult;
import com.infina.pricesim.metrics.ExpectedResultCalculator;
import com.infina.pricesim.metrics.StatsCollector;
import com.infina.pricesim.model.PriceUpdateTask;

@Service
public class SimCoordinator {

	private final CoinService coinService;
	private final TaskGenerator taskGenerator;
	private final ICounter safeCounter;
	private final ICounter unsafeCounter;
	
	private final ExpectedResultCalculator expectedResultCalculator;
	private final StatsCollector statsCollector;
	
	private final SimulationResultStore simResultStore;
	
	private final SimulationLock simulationLock;
	
	public SimCoordinator(
			CoinService coinService,
			TaskGenerator taskGenerator,
			@Qualifier("safeCounter") ICounter safeCounter,
		    @Qualifier("unsafeCounter") ICounter unsafeCounter,
		    ExpectedResultCalculator expectedResultCalculator,
		    StatsCollector statsCollector,
		    SimulationResultStore simResultStore,
		    SimulationLock simulationLock
			) {
		super();
		this.coinService = coinService;
		this.taskGenerator = taskGenerator;
		this.safeCounter = safeCounter;
		this.unsafeCounter = unsafeCounter;
		
		this.expectedResultCalculator=expectedResultCalculator;
		this.statsCollector=statsCollector;
		
		this.simResultStore=simResultStore;
		
		this.simulationLock=simulationLock;
	}
	
	public void simulate(int updates, int workers, Long seed) throws InterruptedException {
		
		if(!simulationLock.tryLock()) {
			throw new SimulationConflictException("A simulation is already running!");
		}
		
		try {
		resetSimulationState();
		
		long resolvedSeed = resolve(seed);
		
		List<PriceUpdateTask> tasks = taskGenerator.generateTasks(updates, resolvedSeed);
	
		Map<String, ExpectedCoinResult> expectedResults =
				expectedResultCalculator.calculate(tasks);
		
	long safeElapsedMs = executeSafeSimulation(tasks, workers);
		
	long unsafeElapsedMs = executeUnsafeSimulation(tasks, workers);
	
		saveSimulationResult(
				resolvedSeed,
				updates,
				workers,
				safeElapsedMs,
				unsafeElapsedMs,
				expectedResults
				);
		
		} finally {
			simulationLock.unLock();
		}
	}
	
	private void saveSimulationResult(
	        long seed,
	        int updates,
	        int workers,
	        long safeElapsedMs,
	        long unsafeElapsedMs,
	        Map<String, ExpectedCoinResult> expectedResults) {

	    SimulationResponse response =
	            statsCollector.collect(
	                    seed,
	                    updates,
	                    workers,
	                    safeElapsedMs,
	                    unsafeElapsedMs,
	                    expectedResults);

	    simResultStore.save(response);
	}
	
	private void resetSimulationState() {
		// TODO Auto-generated method stub
		this.coinService.reset();
		this.safeCounter.reset();
		this.unsafeCounter.reset();
	}

	private long executeSafeSimulation(
			List<PriceUpdateTask> tasks,
			int workers) throws InterruptedException {
	
		return executeSimulation(tasks, workers, safeCounter,
				task -> coinService.getSafeCoin(task.coinId()).apply(task));
	
	}
	
	private long executeUnsafeSimulation(
			List<PriceUpdateTask> tasks,
			int workers) throws InterruptedException {
	
		return executeSimulation(tasks, workers, unsafeCounter,
				task -> coinService.getUnsafeCoin(task.coinId()).apply(task));
	
	}
	
	private long executeSimulation(
			List<PriceUpdateTask> tasks,
			int workers,
			ICounter counter,
			Consumer<PriceUpdateTask> taskExecutor)
			throws InterruptedException
	{
		long start = System.nanoTime();
		
		TaskQueue taskQueue = createTaskQueue(tasks);
		
		ExecutorService executor = Executors.newFixedThreadPool(workers);
		
		for(int i=0; i<workers; i++) {
			executor.submit(new PriceWorker(taskQueue, counter, taskExecutor));
		}
	
		awaitCompletion(executor);
		
		return TimeUnit.NANOSECONDS.toMillis
				( System.nanoTime()-start );
				
			
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