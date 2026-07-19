package com.infina.pricesim.service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.infina.pricesim.api.dto.response.SimulationResponse;

@Service
public class SimulationResultStore {

	private static final int MAX_HISTORY = 5;
	
	private ConcurrentLinkedDeque<SimulationResponse> simHistory = new ConcurrentLinkedDeque<>();
	
	public void save(SimulationResponse simulationResponse) {
		simHistory.addLast(simulationResponse);
		resize();
	}
	
	public List<SimulationResponse> getSimHistory() {
		return List.copyOf(simHistory);
	}
	
	public void clear() {
		simHistory.clear();
	}
	
	public void resize() {
		while(simHistory.size() > MAX_HISTORY) {
			simHistory.removeFirst();
		}
	}

	public SimulationResponse getLatest() {
		// TODO Auto-generated method stub
		return simHistory.getLast();
	}
	
	
}
