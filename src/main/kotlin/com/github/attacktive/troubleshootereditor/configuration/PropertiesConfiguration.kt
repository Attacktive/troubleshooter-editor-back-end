package com.github.attacktive.troubleshootereditor.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "app")
@ConfigurationPropertiesScan(basePackages = ["com.github.attacktive.troubleshootereditor.configuration"])
data class PropertiesConfiguration(val cors: Cors = Cors()) {
	data class Cors(val origins: List<String> = mutableListOf())
}
