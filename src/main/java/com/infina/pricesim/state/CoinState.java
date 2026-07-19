package com.infina.pricesim.state;

import com.infina.pricesim.model.PriceUpdateTask;

import lombok.Getter;

import java.util.Objects;

// no need object of this class ! 
@Getter
public abstract class CoinState {
	private String id;
	private long initialPrice;
	private long currentPrice;
	private long updateCount;
	private long lastDelta;
	private String lastUpdatedBy;

	public CoinState(String id, long initialPrice) {
		this.id = Objects.requireNonNull(id, "id must not be null");
		this.initialPrice = initialPrice;
		this.currentPrice = initialPrice;
		this.updateCount = 0;
		this.lastDelta = 0;
		this.lastUpdatedBy = "";
	}

	public abstract void apply(PriceUpdateTask task);

	protected void updateState(PriceUpdateTask task) {
		Objects.requireNonNull(task, "task must not be null");

		if (!this.id.equals(task.coinId())) {
			throw new IllegalArgumentException("Task coinId does not match this coin state");
		}

		this.currentPrice += task.delta();	
		this.updateCount++;
		this.lastDelta = task.delta();
		this.lastUpdatedBy = Thread.currentThread().getName();
	}

}