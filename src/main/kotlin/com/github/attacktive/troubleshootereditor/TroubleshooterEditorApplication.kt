package com.github.attacktive.troubleshootereditor

import com.github.attacktive.troubleshootereditor.configuration.CorsPropertiesConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CorsPropertiesConfiguration::class)
class TroubleshooterEditorApplication

fun main(args: Array<String>) {
	runApplication<TroubleshooterEditorApplication>(*args)
}
