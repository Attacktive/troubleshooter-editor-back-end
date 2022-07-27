package com.github.attacktive.troubleshootereditor.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class SaveData(
	val company: Company,
	val quests: List<Quest>,
	val rosters: List<Roster>,
	val items: List<Item>
) {
	override fun toString(): String {
		return jacksonObjectMapper().writeValueAsString(this)
	}
}
