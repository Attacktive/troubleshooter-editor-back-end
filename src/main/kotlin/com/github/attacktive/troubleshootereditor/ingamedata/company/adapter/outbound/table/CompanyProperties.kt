package com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound.table

import org.jetbrains.exposed.sql.Table

object CompanyProperties: Table("companyProperty") {
	val companyId = integer("companyID") references Companies.id
	val masterIndex = integer("masterIndex")
	val cpValue = varchar("cpValue", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(companyId, masterIndex)
}
