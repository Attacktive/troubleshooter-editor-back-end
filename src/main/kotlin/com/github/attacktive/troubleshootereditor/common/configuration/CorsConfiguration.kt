package com.github.attacktive.troubleshootereditor.common.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration(private val propertiesConfiguration: PropertiesConfiguration): WebMvcConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		propertiesConfiguration.cors.origins.forEach {
			registry.addMapping("/**").allowedOrigins(it)
		}
	}
}
