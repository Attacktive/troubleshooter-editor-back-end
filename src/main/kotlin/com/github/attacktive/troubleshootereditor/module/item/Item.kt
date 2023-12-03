package com.github.attacktive.troubleshootereditor.module.item

import java.util.stream.Stream
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.attacktive.troubleshootereditor.common.diff.PropertiesDiffResult
import com.github.attacktive.troubleshootereditor.module.Identifiable
import com.github.attacktive.troubleshootereditor.common.diff.DiffResult as IDiffResult

data class Item(val id: Long, val type: String, val count: Long, val status: String): Identifiable<Long> {
	constructor(id: Long, type: String, count: Long, status: String, json: String): this(id, type, count, status) {
		properties = deserialize(json)
	}

	private var properties = mapOf<String, String>()

	@JsonIgnore
	override fun getId(): Long {
		return id
	}

	companion object {
		private fun deserialize(json: String): Map<String, String> {
			val objectMapper = jacksonObjectMapper()
			return objectMapper.readValue(json)
		}
	}

	data class DiffResult(val id: Long, val type: String?, val count: Long?, val status: String?, override val properties: PropertiesDiffResult? = null): IDiffResult, Identifiable<Long> {
		override val hasChanges = Stream.of(type, count, status).anyMatch { it != null }

		@JsonIgnore
		override fun getId(): Long {
			return id
		}
	}
}
