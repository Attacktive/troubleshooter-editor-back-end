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

	fun clearExisting(id: Long) = ItemProperties.deleteWhere { itemId eq id }

	fun insertIsNew(id: Long, isNew: Boolean = false) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.IS_NEW.index
		it[value] = isNew.toString()
	}

	fun insertOptionKey(id: Long, optionKey: String = "Extreme") = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.OPTION_KEY.index
		it[value] = optionKey
	}

	fun insertIsBound(id: Long, isBound: Boolean = true) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.BOUND.index
		it[value] = isBound.toString()
	}

	fun insertRatio(id: Long, ratio: Float = 1F) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.RATIO.index
		it[value] = ratio.toString()
	}

	fun insertIsProtected(id: Long, isProtected: Boolean = true) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.PROTECTED.index
		it[value] = isProtected.toString()
	}

	fun insertLevel(id: Long, level: Int = 9) = ItemProperties.insert {
		it[itemId] = id
		it[masterIndex] = PropertyMaster.LEVEL.index
		it[value] = level.toString()
	}
}
