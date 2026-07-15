package com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table

import org.jetbrains.exposed.v1.core.Table

object ItemStatusMaster: Table("itemStatusMaster") {
	val index = integer("masterIndex")
	val name = varchar("masterName", Int.MAX_VALUE)
}
