package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import java.sql.PreparedStatement
import com.fasterxml.jackson.annotation.JsonGetter
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware

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

		return properties.containsKeyThat{ it.matches(Regex("Option/.+", RegexOption.IGNORE_CASE)) }
	}

	data class DiffResult(val id: Long, val type: String?, val count: Long?, val status: String?, override val properties: Properties): PropertiesDiffAware {
		override fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: MutableList<PreparedStatement> = mutableListOf()

			if (type != null) {
				statements.add(updateStatementForType(connection))
			}

			if (count != null) {
				statements.add(updateStatementForCount(connection))
			}

			if (status != null) {
				statements.add(updateStatementForStatus(connection))
			}

			getStatementsForProperties(connection).forEach { statements.add(it) }

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
				set propValue = '$propertyValue'
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
