package com.github.attacktive.troubleshootereditor.domain.common

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.attacktive.troubleshootereditor.domain.company.Company
import com.github.attacktive.troubleshootereditor.domain.item.Item
import com.github.attacktive.troubleshootereditor.domain.roster.Roster

data class SaveData(val company: Company, val rosters: List<Roster>, val items: List<Item>) {
	override fun toString(): String = jacksonObjectMapper().writeValueAsString(this)

	@JsonGetter
	fun gears() = items.filter { it.isGear() }

	@JsonGetter
	fun consumables() = items.filter { !it.isGear() }
}
