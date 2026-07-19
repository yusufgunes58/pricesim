package com.infina.pricesim.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;
import com.infina.pricesim.model.PriceUpdateTask;
import com.infina.pricesim.service.CoinService;

@Component
public class TaskGenerator {

	private final CoinService coinService;
	private final TaskRandomizer taskRandomizer;

	public TaskGenerator(CoinService coinService, TaskRandomizer taskRandomizer) {
		this.coinService = coinService;
		this.taskRandomizer = taskRandomizer;
	}

	public List<PriceUpdateTask> generateTasks(int updates, long seed) {

		List<String> coinIds = coinService.getCoinIds();

		if (coinIds.isEmpty()) {
			throw new IllegalStateException("No coins.");
		}

		Random random = taskRandomizer.createRandom(seed);

		List<PriceUpdateTask> tasks = new ArrayList<>(updates);

		for (int sequence = 1; sequence <= updates; sequence++) {
			tasks.add(
					createTask(sequence, coinIds, random)
					);
		}

		return tasks;
	}

	private PriceUpdateTask createTask(int sequence, List<String> coinIds, Random random) {

		return new PriceUpdateTask(
				sequence,
				taskRandomizer.randomCoin(coinIds, random),
				taskRandomizer.randomDelta(random)
				);
	}


}