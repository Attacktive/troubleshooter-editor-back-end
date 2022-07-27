package com.github.attacktive.troubleshootereditor.upload

import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.sqlite.SqliteService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class UploadController(private val uploadService: UploadService, private val sqliteService: SqliteService) {
	@PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(@RequestPart("file") multipartFile: MultipartFile): SaveData {
		val savedFileName = uploadService.saveFile(multipartFile)
		val saveData = sqliteService.read(savedFileName)
		uploadService.deleteFile(savedFileName)

		return saveData
	}
}
