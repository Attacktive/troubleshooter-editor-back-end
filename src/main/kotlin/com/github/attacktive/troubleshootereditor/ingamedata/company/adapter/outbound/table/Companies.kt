package com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound.table

import org.jetbrains.exposed.sql.Table

object Companies: Table("company") {
	val id = integer("companyID")
	val name = varchar("CompanyName", Int.MAX_VALUE)
	val vill = long("Vill").default(0)

	override val primaryKey = PrimaryKey(id)
}
