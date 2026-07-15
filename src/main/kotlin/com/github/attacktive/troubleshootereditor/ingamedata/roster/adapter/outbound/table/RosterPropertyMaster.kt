package com.github.attacktive.troubleshootereditor.ingamedata.roster.adapter.outbound.table

import org.jetbrains.exposed.v1.core.Table

object RosterPropertyMaster: Table("rosterPropertyMaster") {
	val index = integer("masterIndex")
	val name = varchar("masterName", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(index)
}
