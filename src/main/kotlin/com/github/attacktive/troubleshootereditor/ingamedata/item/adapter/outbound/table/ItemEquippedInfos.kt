package com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table

import org.jetbrains.exposed.sql.Table

object ItemEquippedInfos: Table("itemEquippedInfo") {
	val itemId = long("itemID") references Items.id
	val positionKey = varchar("positionKey", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(itemId)
}
