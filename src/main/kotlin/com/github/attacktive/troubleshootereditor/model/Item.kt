package com.github.attacktive.troubleshootereditor.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class Item(val id: Long, val type: String, val count: Long, val status: String) {
	constructor(id: Long, type: String, count: Long, status: String, json: String): this(id, type, count, status) {
		properties = deserialize(json)
	}

	var properties = mapOf<String, String>()

	companion object {
		private fun deserialize(json: String): Map<String, String> {
			val objectMapper = jacksonObjectMapper()
			return objectMapper.readValue(json)
		}
	}
}
