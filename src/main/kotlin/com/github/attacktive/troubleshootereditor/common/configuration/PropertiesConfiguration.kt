package com.github.attacktive.troubleshootereditor.common.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan
class PropertiesConfiguration {
	val cors: Cors = Cors()
	val file: File = File()

	class Cors {
		val origins: List<String> = mutableListOf()
	}

	class File {
		var pathToUpload = ""
			set(value) {
				field = value
			}
	}
}
