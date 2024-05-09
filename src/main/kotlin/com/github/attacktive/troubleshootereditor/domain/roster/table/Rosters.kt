package com.github.attacktive.troubleshootereditor.domain.roster.table

import org.jetbrains.exposed.sql.Table

object Rosters: Table("roster") {
	val id = long("rosterID")
	val `class` = varchar("rosterClass", Int.MAX_VALUE)
	val name = varchar("rosterName", Int.MAX_VALUE)
	val exp = long("rosterExp").default(0)
	val level = long("rosterLv").default(0)

	override val primaryKey = PrimaryKey(id)
}
