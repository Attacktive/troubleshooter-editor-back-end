package com.github.attacktive.troubleshootereditor.domain.item

import com.fasterxml.jackson.annotation.JsonGetter
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.IDiffResult
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.item.table.ItemProperties
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

data class Item(val id: Long, val type: String, val count: Long, val status: String, val properties: Properties, var equipmentPosition: EquipmentPosition? = null): Diffable<Item, Long, Item.DiffResult> {
	constructor(id: Long, type: String, count: Long, status: String, properties: Map<String, String>, equipmentPosition: EquipmentPosition? = null): this(id, type, count, status, Properties(properties), equipmentPosition)

	override fun getId() = id

	override fun diff(that: Item): DiffResult {
		val type = that.type.takeUnless { type == that.type }
		val count = that.count.takeUnless { count == that.count }
		val status = that.status.takeUnless { status == that.status }
		val properties = properties.diff(that.properties)

		return DiffResult(id, type, count, status, properties)
	}

	@JsonGetter(value = "properties")
	fun properties() = properties.toMap().toSortedMap()

	fun isGear(): Boolean {
		if (properties.containsKey("Binded") || properties.containsKey("Lv") || properties.containsKey("Transmog")) {
			return true
		}

		return properties.containsKeyThat { it.matches(Regex("Option/.+", RegexOption.IGNORE_CASE)) }
	}

	data class DiffResult(override val id: Long, val type: String?, val count: Long?, val status: String?, val properties: Properties): IDiffResult<Long> {
		override fun insert(propertyIndex: Int, propertyValue: String) {
			ItemProperties.insert {
				it[itemId] = id
				it[masterIndex] = propertyIndex
				it[value] = propertyValue
			}
		}

		override fun update(propertyIndex: Int, propertyValue: String) {
			ItemProperties.update({ (ItemProperties.itemId eq id) and (ItemProperties.masterIndex eq propertyIndex) }) {
				it[value] = propertyValue
			}
		}

		override fun delete(propertyIndex: Int) {
			ItemProperties.deleteWhere {
				(itemId eq id) and (masterIndex eq propertyIndex)
			}
		}
	}
}
