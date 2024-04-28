package com.github.attacktive.troubleshootereditor.domain.item.table

import org.jetbrains.exposed.sql.Table

object Items: Table("item") {
	val id= long("itemID")
	val type= varchar("ItemType", Int.MAX_VALUE)
	val count= long("ItemCount").default(0)
	val status= integer("itemStatus") references ItemStatusMaster.index

	override val primaryKey = PrimaryKey(id)
}
