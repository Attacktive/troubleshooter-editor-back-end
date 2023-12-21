package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.ResultSet
import java.sql.SQLException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

data class Item(val id: Long, val type: String, val count: Long, val status: String, var equipmentPosition: EquipmentPosition? = null) {
	constructor(id: Long, type: String, count: Long, status: String, json: String, equipmentPosition: EquipmentPosition? = null): this(id, type, count, status, equipmentPosition) {
		properties = deserialize(json)
	}

	/**
	 * The property needs to be serialized so don't make it `private`.
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	var properties = mapOf<String, String>()

	companion object {
		fun fromResultSet(resultSet: ResultSet): Item {
			val id = resultSet.getLong("itemID")
			val type = resultSet.getString("itemType")
			val count = resultSet.getLong("itemCount")
			val status = resultSet.getString("masterName")
			val properties = resultSet.getString("properties")
			var equipmentPosition: EquipmentPosition? = null
			try {
				val positionKey = resultSet.getString("positionKey")
				equipmentPosition = EquipmentPosition.fromValue(positionKey)
			} catch (_: SQLException) { }

			return Item(id, type, count, status, properties, equipmentPosition)
		}

		private fun deserialize(json: String): Map<String, String> {
			val objectMapper = jacksonObjectMapper()
			return objectMapper.readValue(json)
		}
	}
}
