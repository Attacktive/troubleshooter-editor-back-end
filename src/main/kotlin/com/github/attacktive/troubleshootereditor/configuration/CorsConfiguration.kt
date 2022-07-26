package com.github.attacktive.troubleshootereditor.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration(private val corsPropertiesConfiguration: CorsPropertiesConfiguration): WebMvcConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		corsPropertiesConfiguration.origins.forEach {
			registry.addMapping("/**").allowedOrigins(it)
		}
	}
}
