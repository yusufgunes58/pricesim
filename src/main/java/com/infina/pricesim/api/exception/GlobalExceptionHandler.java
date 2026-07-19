package com.infina.pricesim.api.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SimulationConflictException.class)
	public ResponseEntity<ErrorResponse> handleSimulationConflict(SimulationConflictException ex,
			HttpServletRequest req) {

		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {

		return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
	}

	@ExceptionHandler(SimulationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleSimulationNotFound(SimulationNotFoundException exception,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidation(HandlerMethodValidationException ex, HttpServletRequest req) {

		String message = ex.getAllErrors().getFirst().getDefaultMessage();

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, req.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest req) {

		ex.printStackTrace();

		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occured!",
				req.getRequestURI());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {

		String message = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).findFirst()
				.orElse("Invalid request parameter.");

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {

		ErrorResponse res = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message,
				path);
		return ResponseEntity.status(status).body(res);
	}

}
