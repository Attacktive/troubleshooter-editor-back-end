package com.github.attacktive.troubleshootereditor.upload

import com.github.attacktive.troubleshootereditor.configuration.PropertiesConfiguration
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Service
class UploadService(private val propertiesConfiguration: PropertiesConfiguration) {
	fun saveFile(multipartFile: MultipartFile): String {
		val directory = File(propertiesConfiguration.file.pathToUpload)
		if (!directory.exists()) {
			directory.mkdirs()
		}

		val fileName = "${UUID.randomUUID()}.db"
		val file = File(directory, fileName)
		val fileOutputStream = FileOutputStream(file)
		fileOutputStream.use {
			it.write(multipartFile.bytes)
		}

		return fileName
	}
}
