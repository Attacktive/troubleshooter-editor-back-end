package com.github.attacktive.troubleshootereditor.endpoint

import java.io.File
import com.github.attacktive.troubleshootereditor.file.UploadService
import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.sqlite.SqliteService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class Controller(private val uploadService: UploadService, private val sqliteService: SqliteService) {
	@PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(@RequestPart("file") multipartFile: MultipartFile): SaveData {
		val savedFileName = uploadService.saveFile(multipartFile)
		val saveData = sqliteService.read(savedFileName)
		uploadService.deleteFile(savedFileName)

		return saveData
	}

	@PostMapping(value = ["/quick-cheats"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun quickCheats(@RequestPart("file") multipartFile: MultipartFile): ResponseEntity<ByteArray> {
		val savedFileName = uploadService.saveFile(multipartFile)
		val fileAbsolutePath = sqliteService.applyQuickCheats(savedFileName)
		val fileInBytes = File(fileAbsolutePath).readBytes()

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(fileInBytes)
	}
}
