package com.github.attacktive.troubleshootereditor.endpoint

import com.github.attacktive.troubleshootereditor.extension.logger
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerExceptionHandler {
	val logger by logger()

	@ExceptionHandler(HttpMessageConversionException::class)
	fun handleHttpMessageConversionException(exception: HttpMessageConversionException): ResponseEntity<String> {
		logger.error(exception.message, exception)

		return ResponseEntity.badRequest().body(exception.message)
	}
}
