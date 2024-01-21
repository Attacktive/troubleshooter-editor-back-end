package com.github.attacktive.troubleshootereditor.file

import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import com.github.attacktive.troubleshootereditor.extension.logger
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UploadService {
	companion object {
		private val tmpdir = System.getProperty("java.io.tmpdir")
	}

	private val logger by logger()

	fun saveFile(multipartFile: MultipartFile): String {
		val directory = File(tmpdir)
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
		val directory = File(tmpdir)
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
