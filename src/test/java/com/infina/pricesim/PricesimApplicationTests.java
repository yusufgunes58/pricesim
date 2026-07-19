package com.infina.pricesim;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.infina.pricesim.api.exception.SimulationConflictException;
import com.infina.pricesim.engine.SimulationLock;

@SpringBootTest
class PricesimApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	void shouldThrowConflictWhenSimulationAlreadyRunning() {

	    SimulationLock lock = new SimulationLock();

	    assertTrue(lock.tryLock());

	    assertThrows(
	            SimulationConflictException.class,
	            () -> {

	                if (!lock.tryLock()) {
	                    throw new SimulationConflictException(
	                            "Another simulation is already running!");
	                }

	            });
	}
	
	@Test
	void shouldLockOnlyOnce() {

	    SimulationLock lock = new SimulationLock();

	    assertTrue(lock.tryLock());

	    assertFalse(lock.tryLock());

	    lock.unlock();

	    assertTrue(lock.tryLock());
	}

}
