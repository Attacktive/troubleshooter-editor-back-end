package com.github.attacktive.troubleshootereditor.domain.item.table

import org.jetbrains.exposed.sql.Table

object ItemEquippedInfos: Table("itemEquippedInfo") {
	val itemId = long("itemID")
	val positionKey = varchar("positionKey", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(itemId)
}
