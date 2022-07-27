package com.github.attacktive.troubleshootereditor.model

data class Quest(val index: Long, val name: String, val stage: Long) {
	val properties: MutableMap<String, String> = mutableMapOf()

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (javaClass != other?.javaClass) {
			return false
		}

		other as Quest
		return index != other.index
	}

	override fun hashCode(): Int {
		return index.hashCode()
	}
}
