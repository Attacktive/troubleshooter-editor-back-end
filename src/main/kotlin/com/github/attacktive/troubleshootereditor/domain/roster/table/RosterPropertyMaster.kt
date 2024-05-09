package com.github.attacktive.troubleshootereditor.domain.roster.table

import org.jetbrains.exposed.sql.Table

object RosterPropertyMaster: Table("rosterPropertyMaster") {
	val index = integer("masterIndex")
	val name = varchar("masterName", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(index)
}
