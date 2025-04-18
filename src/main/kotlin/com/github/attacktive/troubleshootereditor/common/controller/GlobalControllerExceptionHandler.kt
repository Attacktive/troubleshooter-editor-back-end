package com.github.attacktive.troubleshootereditor.common.controller

import com.github.attacktive.troubleshootereditor.common.extension.logger
import com.github.attacktive.troubleshootereditor.ingamedata.common.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalControllerExceptionHandler {
	private val logger by logger()

	@ExceptionHandler(NoResourceFoundException::class)
	fun handleNoResourceFoundException(exception: NoResourceFoundException): ResponseEntity<ErrorResponse> {
		logger.error("Resource not found: /{}", exception.resourcePath)

		return ResponseEntity(ErrorResponse(exception), HttpStatus.NOT_FOUND)
	}

	@ExceptionHandler(HttpMessageConversionException::class)
	fun handleHttpMessageConversionException(exception: HttpMessageConversionException): ResponseEntity<ErrorResponse> {
		logger.error(exception.message, exception)

		return ResponseEntity.badRequest().body(ErrorResponse(exception))
	}

	@ExceptionHandler(Exception::class)
	fun handleUncaughtException(exception: Exception): ResponseEntity<ErrorResponse> {
		logger.error(exception.message, exception)

		return ResponseEntity.internalServerError().body(ErrorResponse(exception))
	}
}
