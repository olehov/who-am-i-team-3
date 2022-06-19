package com.eleks.academy.whoami.configuration;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.eleks.academy.whoami.core.exception.ErrorResponse;
import com.eleks.academy.whoami.core.exception.GameException;
import com.eleks.academy.whoami.core.exception.GameNotFoundException;
import com.eleks.academy.whoami.core.exception.PlayerAlreadyInGameException;
import com.eleks.academy.whoami.model.response.ApiError;

@RestControllerAdvice
public class GameControllerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(GameException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleGameException(GameException e) {
		return e::getMessage;
	}
	
	@ExceptionHandler(GameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleGameNotFoundException(GameNotFoundException e) {
		return e::getMessage;
	}
	
	@ExceptionHandler(PlayerAlreadyInGameException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiError handlePlayerAlreadyInGameException(PlayerAlreadyInGameException e) {
		return e::getMessage;
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
																  HttpHeaders headers, HttpStatus status,
																  WebRequest request) {
		return e.getBindingResult().getAllErrors()
				.stream()
				.map(ObjectError::getDefaultMessage)
				.collect(collectingAndThen(
						toList(),
						details -> ResponseEntity.badRequest()
								.body(new ErrorResponse("Validation failed!", details))
				));
	}

	
}
