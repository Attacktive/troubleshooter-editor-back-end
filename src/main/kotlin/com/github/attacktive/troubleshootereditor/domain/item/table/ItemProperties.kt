package com.github.attacktive.troubleshootereditor.domain.item.table

import com.github.attacktive.troubleshootereditor.domain.item.PropertyMaster
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

object ItemProperties: Table("itemProperty") {
	val itemId = long("itemID") references Items.id
	val masterIndex = integer("masterIndex") references ItemPropertyMaster.index
	val value = varchar("propValue", Int.MAX_VALUE)

	override val primaryKey = PrimaryKey(itemId, masterIndex)

	fun applyDefaultChanges(id: Long) {
		clearExisting(id)
		insertIsNew(id)
		insertOptionKey(id)
		insertIsBound(id)
		insertRatio(id)
		insertIsProtected(id)
		insertLevel(id)
	}

	private fun clearExisting(id: Long) = ItemProperties.deleteWhere { itemId eq id }

	private fun insertIsNew(id: Long, isNew: Boolean = false) = insert(id, PropertyMaster.IS_NEW, isNew)

	private fun insertOptionKey(id: Long, optionKey: String = "Extreme") = insert(id, PropertyMaster.OPTION_KEY, optionKey)

	private fun insertIsBound(id: Long, isBound: Boolean = true) = insert(id, PropertyMaster.BOUND, isBound)

	private fun insertRatio(id: Long, ratio: Float = 1F) = insert(id, PropertyMaster.RATIO, ratio)

	private fun insertIsProtected(id: Long, isProtected: Boolean = true) = insert(id, PropertyMaster.PROTECTED, isProtected)

	private fun insertLevel(id: Long, level: Int = 9) = insert(id, PropertyMaster.LEVEL, level)

	fun insert(id: Long, propertyMaster: PropertyMaster, propertyValue: Boolean) = insert(id, propertyMaster, propertyValue.toString())
	fun insert(id: Long, propertyMaster: PropertyMaster, propertyValue: Number) = insert(id, propertyMaster, propertyValue.toString())
	fun insert(id: Long, propertyMaster: PropertyMaster, propertyValue: String) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = propertyMaster.index
		it[value] = propertyValue
	}
}
