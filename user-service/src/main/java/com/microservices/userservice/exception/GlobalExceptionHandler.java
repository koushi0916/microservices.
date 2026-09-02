package com.microservices.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(buildError(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> validationErrors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			validationErrors.put(error.getField(), error.getDefaultMessage());
		}

		ApiErrorResponse errorResponse = buildError(HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
		errorResponse.setValidationErrors(validationErrors);
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(buildError(HttpStatus.BAD_REQUEST, "Request body must be valid JSON with Content-Type: application/json", request.getRequestURI()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unhandled error on {}", request.getRequestURI(), ex);
		String details = ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "no message" : ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, details, request.getRequestURI()));
	}

	private ApiErrorResponse buildError(HttpStatus status, String message, String path) {
		return new ApiErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				path
		);
	}
}