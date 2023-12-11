package com.github.attacktive.troubleshootereditor.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan
class PropertiesConfiguration {
	val cors: Cors = Cors()

	class Cors {
		val origins: List<String> = mutableListOf()
	}
}
