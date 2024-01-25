package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesAware
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware
import com.github.attacktive.troubleshootereditor.extension.deserializeAsStringToStringMap

data class Item(val id: Long, val type: String, val count: Long, val status: String, var equipmentPosition: EquipmentPosition? = null): Diffable<Item, Long, Item.DiffResult>, PropertiesAware {
	constructor(id: Long, type: String, count: Long, status: String, propertiesJson: String, equipmentPosition: EquipmentPosition? = null): this(id, type, count, status, equipmentPosition) {
		addProperties(propertiesJson.deserializeAsStringToStringMap())
	}

	override val properties: Properties = Properties()

	override fun getId() = id

	companion object {
		fun fromResultSet(resultSet: ResultSet): List<Item> {
			val items: List<Item> = mutableListOf()

			while (resultSet.next()) {
				val id = resultSet.getLong("itemID")
				val type = resultSet.getString("itemType")
				val count = resultSet.getLong("itemCount")
				val status = resultSet.getString("masterName")
				val properties = resultSet.getString("properties")

				var equipmentPosition: EquipmentPosition? = null
				try {
					val positionKey = resultSet.getString("positionKey")
					equipmentPosition = EquipmentPosition.fromValue(positionKey)
				} catch (sqlException: SQLException) {
					val isExpectedException = Regex(".*no such column.*", RegexOption.IGNORE_CASE).matches(sqlException.message ?: "")
					if (!isExpectedException) {
						throw sqlException
					}
				}

				items.addLast(Item(id, type, count, status, properties, equipmentPosition))
			}

			return items
		}
	}

	override fun diff(that: Item): DiffResult {
		val type = that.type.takeUnless { type == that.type }
		val count = that.count.takeUnless { count == that.count }
		val status = that.status.takeUnless { status == that.status }
		val properties = properties.diff(that.properties)

		return DiffResult(id, type, count, status, properties)
	}

	data class DiffResult(val id: Long, val type: String?, val count: Long?, val status: String?, override val properties: Properties): PropertiesDiffAware {
		override fun generateStatements(connection: Connection): List<PreparedStatement> {
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
	}
}
