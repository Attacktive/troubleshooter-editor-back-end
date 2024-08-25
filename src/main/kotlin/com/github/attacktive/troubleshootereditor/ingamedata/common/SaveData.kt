package com.github.attacktive.troubleshootereditor.ingamedata.common

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.attacktive.troubleshootereditor.ingamedata.company.domain.Company
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.Item
import com.github.attacktive.troubleshootereditor.ingamedata.roster.domain.Roster

data class SaveData(val company: Company, val rosters: List<Roster>, val items: List<Item>) {
	override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)

	@JsonGetter
	fun gears() = items.filter { it.isGear() }

	@JsonGetter
	fun consumables() = items.filter { !it.isGear() }
}
