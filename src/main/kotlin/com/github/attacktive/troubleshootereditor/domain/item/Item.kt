package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesAware
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware

data class Item(val id: Long, val type: String, val count: Long, val status: String, var equipmentPosition: EquipmentPosition? = null): PropertiesAware {
	constructor(id: Long, type: String, count: Long, status: String, json: String, equipmentPosition: EquipmentPosition? = null): this(id, type, count, status, equipmentPosition) {
		addProperties(deserialize(json))
	}

	override val properties: Properties = Properties()

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

	fun diff(that: Item): DiffResult {
		val type = that.type.takeUnless { type == that.type }
		val count = that.count.takeUnless { count == that.count }
		val status = that.status.takeUnless { status == that.status }

		val properties = properties.diff(that.properties)

		return DiffResult(id, type, count, status, properties)
	}

	data class DiffResult(val id: Long, val type: String?, val count: Long?, val status: String?, override val properties: Properties): PropertiesDiffAware {
		fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: List<PreparedStatement> = mutableListOf()

			if (type != null) {
				statements.addLast(updateStatementForType(connection))
			}

			if (count != null) {
				statements.addLast(updateStatementForCount(connection))
			}

			if (status != null) {
				statements.addLast(updateStatementForStatus(connection))
			}

			getStatementsForProperties(connection).forEach { statements.addLast(it) }

			return statements
		}

		private fun updateStatementForType(connection: Connection) = connection.prepareStatement("""
			update item
			set itemType = $type
			where itemID = $id
			""".trimIndent()
		)

		private fun updateStatementForCount(connection: Connection) = connection.prepareStatement("""
			update item
			set itemCount = $count
			where itemID = $id
			""".trimIndent()
		)

		private fun updateStatementForStatus(connection: Connection) = connection.prepareStatement("""
			update item
			set itemStatus = $status
			where itemID = $id
			""".trimIndent()
		)

		override fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement("""
				insert into itemProperty (itemID, masterIndex, propValue)
				select
					$id,
					ipm.masterIndex,
					'$propertyValue'
				from itemPropertyMaster ipm
				where masterName = '$propertyName'
			""".trimIndent()
		)

		override fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement("""
				update itemProperty
				set cpValue = '$propertyValue'
				where itemID = $id and masterIndex = (
					select masterIndex
					from itemPropertyMaster
					where masterName = '$propertyName'
				)
			""".trimIndent()
		)

		override fun deleteStatementForProperty(connection: Connection, propertyName: String): PreparedStatement = connection.prepareStatement("""
				delete from itemProperty
				where masterIndex = (
					select masterIndex
					from itemPropertyMaster
					where itemID = $id and masterName = '$propertyName'
				)
			""".trimIndent()
		)
	}
}
