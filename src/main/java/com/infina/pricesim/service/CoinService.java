package com.infina.pricesim.service;

import com.infina.pricesim.state.SafeCoinState;
import com.infina.pricesim.state.UnsafeCoinState;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoinService {

	private final String BTC = "BTC";
	private final String ETH = "ETH";
	private final String SOL = "SOL";

	private final long BTC_INITIAL_PRICE = 60_000L;
	private final long ETH_INITIAL_PRICE = 3_000L;
	private final long SOL_INITIAL_PRICE = 150L;

	private final Map<String, SafeCoinState> safeCoins = new ConcurrentHashMap<>();
	private final Map<String, UnsafeCoinState> unsafeCoins = new ConcurrentHashMap<>();

	@PostConstruct
	private void initializeCoins() {
		addCoin(BTC, BTC_INITIAL_PRICE);
		addCoin(ETH, ETH_INITIAL_PRICE);
		addCoin(SOL, SOL_INITIAL_PRICE);
	}

	public void addCoin(String coinId, long initialPrice) {
		safeCoins.put(coinId, new SafeCoinState(coinId, initialPrice));
		unsafeCoins.put(coinId, new UnsafeCoinState(coinId, initialPrice));
	}

	public SafeCoinState getSafeCoin(String coinId) {
		return safeCoins.get(coinId);
	}

	public UnsafeCoinState getUnsafeCoin(String coinId) {
		return unsafeCoins.get(coinId);
	}

	public void reset() {
		safeCoins.clear();
		unsafeCoins.clear();
		initializeCoins();
	}

	// return a copy of the safe coins and unsafe coins to external callers

	public List<SafeCoinState> getSafeCoins() {
		return List.copyOf(safeCoins.values());
	}

	public List<UnsafeCoinState> getUnsafeCoins() {
		return List.copyOf(unsafeCoins.values());
	}

	public List<String> getCoinIds() {
		if (!compareCoinMaps()) {
			throw new IllegalStateException("Safe and unsafe coin maps are not consistent");
		}

		return List.copyOf(safeCoins.keySet());
	}

	private boolean compareCoinMaps() {

		if (safeCoins.size() != unsafeCoins.size()) {
			return false;
		}

		if (!safeCoins.keySet().equals(unsafeCoins.keySet())) {
			return false;
		}

		return safeCoins.values().stream()
				.allMatch(safe -> safe.getInitialPrice() == unsafeCoins.get(safe.getId()).getInitialPrice());

	}

}