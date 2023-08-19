package com.github.attacktive.troubleshootereditor.model

import java.sql.Connection
import java.sql.PreparedStatement
import java.util.stream.Stream
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.attacktive.troubleshootereditor.model.DiffResult as IDiffResult

data class Item(val id: Long, val type: String, val count: Long, val status: String) {
	constructor(id: Long, type: String, count: Long, status: String, json: String): this(id, type, count, status) {
		properties = deserialize(json)
	}

	private var properties = mapOf<String, String>()

	companion object {
		private fun deserialize(json: String): Map<String, String> {
			val objectMapper = jacksonObjectMapper()
			return objectMapper.readValue(json)
		}
	}

	data class DiffResult(private val id: Long, val type: String?, val count: Long?, val status: String?, val properties: PropertiesDiffResult?): IDiffResult {
		override val hasChanges = Stream.of(type, count, status).anyMatch { it != null }

		override fun getStatements(connection: Connection): MutableList<PreparedStatement> {
			if (!hasChanges) {
				throw RuntimeException("No statements when no changes.")
			}

			// TODO: use SqliteService#getProperties
			val itemProperties = mutableMapOf<String, Long>()
			val preparedStatement = connection.prepareStatement("select masterIndex, masterName from itemPropertyMaster")
			preparedStatement.executeQuery().use {
				while (it.next()) {
					val propertyId = it.getLong("masterIndex")
					val propertyName = it.getString("masterName")

					itemProperties[propertyName] = propertyId
				}
			}

			var toUpdateItems = false

			val setClauseBuilder = mutableListOf<Pair<String, Any>>()

			if (type != null) {
				setClauseBuilder.add("itemType = ?" to type)
				toUpdateItems = true
			}

			if (count != null) {
				setClauseBuilder.add("itemCount = ?" to count)
				toUpdateItems = true
			}

			if (status != null) {
				setClauseBuilder.add("itemStatus = ?" to status)
				toUpdateItems = true
			}

			var itemStatement: PreparedStatement? = null
			if (toUpdateItems) {
				val setClause = setClauseBuilder.joinToString(", ") { it.first }

				val itemQuery = "update item $setClause where itemID = $id"

				@Suppress("SqlSourceToSinkFlow")
				itemStatement = connection.prepareStatement(itemQuery)

				setClauseBuilder.forEachIndexed { index, pair -> itemStatement.setObject((index + 1), pair.second) }
			}

			val propertyStatements = mutableListOf<PreparedStatement>()
			if (properties != null) {
				propertyStatements.addAll(
					properties.inserts.map {
						val masterIndex = itemProperties[it.first]!!

						val statement = connection.prepareStatement("insert into itemProperty(itemID, masterIndex, propValue) values ($id, ?, ?)")
						statement.setLong(1, masterIndex)
						statement.setString(2, it.second)

						statement
					}
				)

				propertyStatements.addAll(
					properties.updates.map {
						val masterIndex = itemProperties[it.first]!!

						val statement = connection.prepareStatement("update itemProperty set propValue = ? where itemID = $id and masterIndex = ?")
						statement.setString(2, it.second)
						statement.setLong(1, masterIndex)

						statement
					}
				)

				propertyStatements.addAll(
					properties.deletes.map {
						val masterIndex = itemProperties[it]!!

						val statement = connection.prepareStatement("delete from itemProperty where itemID = $id and masterIndex = ?")
						statement.setLong(1, masterIndex)

						statement
					}
				)
			}

			val statements = mutableListOf(itemStatement)
				.filterNotNull()
				.toMutableList()

			statements += propertyStatements

			return statements
		}
	}
}
