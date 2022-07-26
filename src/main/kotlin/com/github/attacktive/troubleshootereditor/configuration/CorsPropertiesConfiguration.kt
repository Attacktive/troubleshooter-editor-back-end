package com.github.attacktive.troubleshootereditor.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "app.cors")
@ConfigurationPropertiesScan
class CorsPropertiesConfiguration {
	val origins: List<String> = mutableListOf()
}
