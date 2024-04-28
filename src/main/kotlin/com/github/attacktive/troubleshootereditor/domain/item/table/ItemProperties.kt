package com.github.attacktive.troubleshootereditor.domain.item.table

import org.jetbrains.exposed.sql.Table

object ItemProperties: Table("itemProperty") {
	val itemId = long("itemID") references Items.id
	val masterIndex = integer("masterIndex") references ItemPropertyMaster.index
	val value = varchar("propValue", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(itemId, masterIndex)
}
