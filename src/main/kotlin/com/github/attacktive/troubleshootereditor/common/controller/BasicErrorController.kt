package com.github.attacktive.troubleshootereditor.common.controller

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import com.github.attacktive.troubleshootereditor.common.extension.logger
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController as SpringBasicErrorController

@Controller
class BasicErrorController(serverProperties: ServerProperties?): SpringBasicErrorController(DefaultErrorAttributes(), serverProperties?.error) {
	private val logger by logger()

	override fun error(request: HttpServletRequest): ResponseEntity<MutableMap<String, Any>> {
		val exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION)
		if (exception is Throwable) {
			logger.error(exception.message, exception)
		}

		return super.error(request)
	}
}
