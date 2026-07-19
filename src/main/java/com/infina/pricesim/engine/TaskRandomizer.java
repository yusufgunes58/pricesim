package com.infina.pricesim.engine;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class TaskRandomizer {

	private static final int MIN_DELTA = 1;
	private static final int MAX_DELTA = 100;
	
	public Random createRandom(long seed) {
		return seed == 0 ? new Random() : new Random(seed);
	}
	
	public String randomCoin(List<String> coinIds, Random random) {
		return coinIds.get(random.nextInt(coinIds.size()));
	}
	
	public long randomDelta(Random random) {
		long delta = random.nextInt(MAX_DELTA - MIN_DELTA + 1) + MIN_DELTA;
		return random.nextBoolean() ? delta : -delta;
	}
	
}
