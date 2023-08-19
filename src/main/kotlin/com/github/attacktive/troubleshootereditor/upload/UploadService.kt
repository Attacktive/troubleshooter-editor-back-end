package com.github.attacktive.troubleshootereditor.upload

import com.github.attacktive.troubleshootereditor.configuration.PropertiesConfiguration
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Service
class UploadService(private val propertiesConfiguration: PropertiesConfiguration) {
	companion object {
		private val logger = LoggerFactory.getLogger(UploadService::class.java)
	}

	fun saveFile(multipartFile: MultipartFile): String {
		val directory = File(propertiesConfiguration.file.pathToUpload)
		if (!directory.exists()) {
			directory.mkdirs()
		}

		val fileName = "${UUID.randomUUID()}.db"
		val file = File(directory, fileName)

		logger.info("Uploaded file ${multipartFile.originalFilename} is temporarily saved as \"${file.absolutePath}\".")

		val fileOutputStream = FileOutputStream(file)
		fileOutputStream.use {
			it.write(multipartFile.bytes)
		}

		return fileName
	}

	fun deleteFile(fileName: String): Boolean {
		val directory = File(propertiesConfiguration.file.pathToUpload)
		val file = File(directory, fileName)

		val deleted = file.delete()
		if (deleted) {
			logger.info("Uploaded file $fileName is just deleted.")
		} else {
			logger.warn("Failed to delete file \"${file.absolutePath}\".")
		}

		return deleted
	}
}
