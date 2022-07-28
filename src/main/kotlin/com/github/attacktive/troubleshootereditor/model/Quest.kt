package com.github.attacktive.troubleshootereditor.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Quest(val index: Long, val name: String, val stage: Long): Identifiable<Long> {
	val properties: MutableMap<String, String> = mutableMapOf()

	@JsonIgnore
	override fun getId(): Long {
		return index
	}
}
