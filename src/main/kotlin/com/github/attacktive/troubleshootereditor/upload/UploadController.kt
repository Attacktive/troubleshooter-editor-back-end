package com.github.attacktive.troubleshootereditor.upload

import com.github.attacktive.troubleshootereditor.configuration.PropertiesConfiguration
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class UploadController(private val uploadService: UploadService, private val sqliteService: SqliteService) {
	private val logger = LoggerFactory.getLogger(UploadController::class.java)

	@PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(@RequestPart("file") multipartFile: MultipartFile): Any {
		logger.info("${multipartFile.name} (size: ${multipartFile.size})")
		val savedFileName = uploadService.saveFile(multipartFile)
		sqliteService.run(savedFileName)

		return mapOf("x" to "y")
	}
}
