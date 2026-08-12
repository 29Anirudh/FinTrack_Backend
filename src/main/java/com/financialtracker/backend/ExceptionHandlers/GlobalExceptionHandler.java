package com.financialtracker.backend.ExceptionHandlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.financialtracker.backend.Exceptions.UserDefinedException;

@ControllerAdvice
public class GlobalExceptionHandler {
	Map<String, Object> resperror=new HashMap<String, Object>();
	@ExceptionHandler(UserDefinedException.class)
	public ResponseEntity<?> userDefinedExceptionHandler(UserDefinedException E){
		resperror.put("msg", E.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resperror);
	}
}
