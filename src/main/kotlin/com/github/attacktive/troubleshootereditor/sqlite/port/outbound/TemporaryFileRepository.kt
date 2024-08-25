package com.github.attacktive.troubleshootereditor.sqlite.port.outbound

import org.springframework.web.multipart.MultipartFile

interface TemporaryFileRepository {
	fun saveToTemporaryDirectory(multipartFile: MultipartFile): String
	fun deleteFile(fileName: String): Boolean
}
