package com.github.attacktive.troubleshootereditor.domain.roster.table

import org.jetbrains.exposed.sql.Table

object RosterProperties: Table("rosterProperty") {
	val rosterId = long("rosterID") references Rosters.id
	val masterIndex = integer("masterIndex") references RosterPropertyMaster.index
	val value = varchar("rpValue", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(rosterId, masterIndex)
}
