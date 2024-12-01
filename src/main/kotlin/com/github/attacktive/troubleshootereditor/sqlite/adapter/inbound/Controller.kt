package com.github.attacktive.troubleshootereditor.sqlite.adapter.inbound

import java.io.File
import com.github.attacktive.troubleshootereditor.ingamedata.common.InboundSaveData
import com.github.attacktive.troubleshootereditor.ingamedata.common.SaveData
import com.github.attacktive.troubleshootereditor.sqlite.adapter.outbound.TemporaryFileService
import com.github.attacktive.troubleshootereditor.sqlite.port.inbound.SqliteUseCase
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class Controller(private val temporaryFileService: TemporaryFileService, private val sqliteUseCase: SqliteUseCase) {
	@GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
	fun hello() = "Hello"

	@PostMapping(value = ["/upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun upload(@RequestPart("file") multipartFile: MultipartFile): SaveData {
		val savedFileName = temporaryFileService.saveToTemporaryDirectory(multipartFile)
		val saveData = sqliteUseCase.readFileByName(savedFileName)

		temporaryFileService.deleteFile(savedFileName)

		return saveData
	}

	@PostMapping(value = ["/save"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun save(@RequestPart("file") multipartFile: MultipartFile, @RequestPart("edited") inboundSaveData: InboundSaveData): ResponseEntity<ByteArray> {
		val savedFileName = temporaryFileService.saveToTemporaryDirectory(multipartFile)
		val editedFileAbsolutePath = sqliteUseCase.save(savedFileName, inboundSaveData)
		val fileInBytes = File(editedFileAbsolutePath).readBytes()

		temporaryFileService.deleteFile(savedFileName)

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(fileInBytes)
	}

	@PostMapping(value = ["/quick-cheats"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun quickCheats(@RequestPart("file") multipartFile: MultipartFile): ResponseEntity<ByteArray> {
		val savedFileName = temporaryFileService.saveToTemporaryDirectory(multipartFile)
		val cheatedFileAbsolutePath = sqliteUseCase.applyQuickCheats(savedFileName)
		val fileInBytes = File(cheatedFileAbsolutePath).readBytes()

		temporaryFileService.deleteFile(savedFileName)

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(fileInBytes)
	}
}
