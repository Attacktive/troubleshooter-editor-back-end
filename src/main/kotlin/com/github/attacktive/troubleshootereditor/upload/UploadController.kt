package com.github.attacktive.troubleshootereditor.upload

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UploadController {
	private val logger = LoggerFactory.getLogger(UploadController::class.java)

	@PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(): Any {
		logger.debug("upload")

		return mapOf("x" to "y")
	}
}
