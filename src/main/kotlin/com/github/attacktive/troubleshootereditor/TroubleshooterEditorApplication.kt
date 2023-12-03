package com.github.attacktive.troubleshootereditor

import com.github.attacktive.troubleshootereditor.common.configuration.PropertiesConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfiguration::class)
class TroubleshooterEditorApplication

fun main(args: Array<String>) {
	runApplication<TroubleshooterEditorApplication>(*args)
}
