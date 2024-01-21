package com.github.attacktive.troubleshootereditor.domain.common

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonSetter

interface PropertiesAware {
	val properties: Properties

	fun addProperty(property: Pair<String, String>) = properties.add(property)

	@JsonGetter(value = "properties")
	fun properties() = properties.toMap()

	@JsonSetter(value = "properties")
	fun properties(properties: Map<String, String>) = addProperties(properties)

	fun addProperties(properties: Map<String, String>) = properties.entries.forEach { addProperty(it.toPair()) }
}
