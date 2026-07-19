package com.infina.pricesim.api.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponse> buildErrorResponse(
			HttpStatus status,
			String message,
			String path
			) {
		
		ErrorResponse res = new ErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				path
				);
			return ResponseEntity.status(status).body(res);
	}
	
	@ExceptionHandler(SimulationConflictException.class) 
	public ResponseEntity<ErrorResponse> handleSimulationConflict(
			SimulationConflictException ex,
			HttpServletRequest req
			) {
		
		return buildErrorResponse(
				HttpStatus.CONFLICT,
				ex.getMessage(),
				req.getRequestURI()
				);
	}
	
	
	@ExceptionHandler(Exception.class) 
	public ResponseEntity<ErrorResponse> handleUnexpectedException(
			Exception ex,
			HttpServletRequest req
			) {
		
		return buildErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occured!",
				req.getRequestURI()
				);
	}
	
	@ExceptionHandler(IllegalArgumentException.class) 
	public ResponseEntity<ErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex,
			HttpServletRequest req
			) {
		
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				ex.getMessage(),
				req.getRequestURI()
				);
	}
	
	  @ExceptionHandler(SimulationNotFoundException.class)
	    public ResponseEntity<ErrorResponse> handleSimulationNotFound(
	            SimulationNotFoundException exception,
	            HttpServletRequest request) {

	        return buildErrorResponse(
	                HttpStatus.NOT_FOUND,
	                exception.getMessage(),
	                request.getRequestURI()
	        );
	    }
	
}
