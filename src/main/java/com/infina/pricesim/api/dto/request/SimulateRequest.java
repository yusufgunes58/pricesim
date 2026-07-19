package com.infina.pricesim.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SimulateRequest(
		@Min(1) 
		@Max(100000)
		int updates,
		
		@Min(1)
		@Max(16)
		int workers,
		
		Long seed
		) {

}
