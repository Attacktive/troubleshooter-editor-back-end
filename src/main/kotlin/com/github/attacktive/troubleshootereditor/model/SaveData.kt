package com.github.attacktive.troubleshootereditor.model

import com.fasterxml.jackson.databind.ObjectMapper

data class SaveData(
	val company: Company,
	val quests: List<Quest>,
	val rosters: List<Roster>
) {
	override fun toString(): String {
		val objectMapper = ObjectMapper()
		return objectMapper.writeValueAsString(this)
	}
}
