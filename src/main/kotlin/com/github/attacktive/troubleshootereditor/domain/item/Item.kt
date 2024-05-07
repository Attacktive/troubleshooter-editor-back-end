package com.github.attacktive.troubleshootereditor.domain.item

import com.fasterxml.jackson.annotation.JsonGetter
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Properties

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

	data class DiffResult(val id: Long, val type: String?, val count: Long?, val status: String?, val properties: Properties)
}
