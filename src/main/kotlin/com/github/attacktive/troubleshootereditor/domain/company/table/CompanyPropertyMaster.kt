package com.github.attacktive.troubleshootereditor.domain.company.table

import org.jetbrains.exposed.sql.Table

object CompanyPropertyMaster: Table("companyPropertyMaster") {
	val masterIndex = integer("masterIndex") references CompanyProperties.masterIndex
	val masterName = varchar("masterName", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(masterIndex)
}
