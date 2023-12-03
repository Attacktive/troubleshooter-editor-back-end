package com.github.attacktive.troubleshootereditor.module

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.attacktive.troubleshootereditor.module.company.Company
import com.github.attacktive.troubleshootereditor.module.item.Item
import com.github.attacktive.troubleshootereditor.module.quest.Quest
import com.github.attacktive.troubleshootereditor.module.roster.Roster

data class SaveData(val company: Company, val quests: List<Quest>, val rosters: List<Roster>, val items: List<Item>) {
	override fun toString(): String {
		return jacksonObjectMapper().writeValueAsString(this)
	}
}
