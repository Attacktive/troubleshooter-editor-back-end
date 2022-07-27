package com.github.attacktive.troubleshootereditor.model

data class Quest(val index: Long, val name: String, val stage: Long): Identifiable<Long> {
	val properties: MutableMap<String, String> = mutableMapOf()

	override fun getId(): Long {
		return index
	}
}