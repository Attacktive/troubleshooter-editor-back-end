package com.github.attacktive.troubleshootereditor.download

import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.upload.UploadService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class DownloadController(private val uploadService: UploadService, private val downloadService: DownloadService) {
	companion object {
		private const val CONTENT_DISPOSITION_VALUE = "attachment;filename=game.sav"
	}

	@PostMapping(value = ["/download"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun download(httpServletResponse: HttpServletResponse, @RequestPart("file") multipartFile: MultipartFile, saveData: SaveData) {
		httpServletResponse.addHeader(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE)

		val savedFileName = uploadService.saveFile(multipartFile)
		uploadService.deleteFile(savedFileName)

		return downloadService.download(savedFileName, saveData)
	}
}
