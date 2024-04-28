package com.github.attacktive.troubleshootereditor.domain.item.table

import org.jetbrains.exposed.sql.Table

object ItemStatusMaster: Table("itemStatusMaster") {
	val index = integer("masterIndex")
	val name = varchar("masterName", Int.MAX_VALUE)
}
