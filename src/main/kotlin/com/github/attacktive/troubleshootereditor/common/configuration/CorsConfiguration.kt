package com.github.attacktive.troubleshootereditor.common.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration(private val propertiesConfiguration: PropertiesConfiguration): WebMvcConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		val corsConfiguration = CorsConfiguration()
		corsConfiguration.allowedOrigins = propertiesConfiguration.cors.origins

		registry.addMapping("/**").combine(corsConfiguration)
	}
}
