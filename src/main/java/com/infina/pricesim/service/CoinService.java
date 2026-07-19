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
		SafeCoinState safeCoin = safeCoins.get(coinId);
		if (safeCoin == null) {
			throw new IllegalArgumentException("Coin ID not found: " + coinId);
		}
		return safeCoin;
	}

	public UnsafeCoinState getUnsafeCoin(String coinId) {
		UnsafeCoinState unsafeCoin = unsafeCoins.get(coinId);
		if (unsafeCoin == null) {
			throw new IllegalArgumentException("Coin ID not found: " + coinId);
		}	
		return unsafeCoin;
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

	public long getInitialPrice(String coinId) {
		SafeCoinState safeCoin = safeCoins.get(coinId);
		if (safeCoin != null) {
			return safeCoin.getInitialPrice();
		}
		throw new IllegalArgumentException("Coin ID not found: " + coinId);
	}
	
	// check for consistency between safeCoins and unsafeCoins maps
	private boolean compareCoinMaps() {
	    return checkSizeConsistency()
	        && checkKeyConsistency()
	        && checkPriceConsistency();
	}

	
	// Check coin maps 
	
	private boolean checkSizeConsistency() {
	    if (safeCoins.size() != unsafeCoins.size()) {
	        System.err.println("Size mismatch: safe=" + safeCoins.size() + ", unsafe=" + unsafeCoins.size());
	        return false;
	    }
	    return true;
	}

	private boolean checkKeyConsistency() {
	    if (!safeCoins.keySet().equals(unsafeCoins.keySet())) {
	        System.err.println("Key mismatch: safeKeys=" + safeCoins.keySet() + ", unsafeKeys=" + unsafeCoins.keySet());
	        return false;
	    }
	    return true;
	}

	private boolean checkPriceConsistency() {
	    boolean pricesMatch = safeCoins.values().stream()
	            .allMatch(safe -> safe.getInitialPrice() == unsafeCoins.get(safe.getId()).getInitialPrice());
	    if (!pricesMatch) {
	        System.err.println("Price mismatch between safe and unsafe coin maps");
	        return false;
	    }
	    return true;
	}


}