package com.github.attacktive.troubleshootereditor.module.item

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.module.common.PropertiesService

object ItemService {
	fun selectItems(connection: Connection): List<Item> {
		val statement = connection.prepareStatement(
			"""
				select
					i.itemID,
					i.itemType,
					i.itemCount,
					ism.masterName,
					iif(ipm.masterIndex is null, '{}', json_group_object(ipm.masterName, ip.propValue)) as properties
				from item i
					left join itemStatusMaster ism on i.itemStatus = ism.masterIndex
					left join itemProperty ip on i.itemID = ip.itemID
					left join itemPropertyMaster ipm on ip.masterIndex = ipm.masterIndex
				group by i.itemID, i.itemType, i.itemCount, ism.masterName
			""".trimIndent()
		)

		val items = mutableSetOf<Item>()
		statement.executeQuery().use {
			while (it.next()) {
				val id = it.getLong("itemID")
				val type = it.getString("itemType")
				val count = it.getLong("itemCount")
				val status = it.getString("masterName")
				val properties = it.getString("properties")

				val item = Item(id, type, count, status, properties)
				items.add(item)
			}
		}

		return items.toList()
	}

	fun getStatements(diffResult: Item.DiffResult, connection: Connection): MutableList<PreparedStatement> {
		if (!diffResult.hasChanges) {
			throw IllegalStateException("No statements when no changes.")
		}

		var toUpdateItems = false

		val setClauseBuilder = mutableListOf<Pair<String, Any>>()

		if (diffResult.type != null) {
			setClauseBuilder.add("itemType = ?" to diffResult.type)
			toUpdateItems = true
		}

		if (diffResult.count != null) {
			setClauseBuilder.add("itemCount = ?" to diffResult.count)
			toUpdateItems = true
		}

		if (diffResult.status != null) {
			setClauseBuilder.add("itemStatus = ?" to diffResult.status)
			toUpdateItems = true
		}

		var itemStatement: PreparedStatement? = null
		if (toUpdateItems) {
			val setClause = setClauseBuilder.joinToString(", ") { it.first }

			val itemQuery = """
				update item
				$setClause
				where itemID = ${diffResult.id}
				""".trimIndent()

			itemStatement = connection.prepareStatement(itemQuery)

			setClauseBuilder.forEachIndexed { index, pair -> itemStatement.setObject((index + 1), pair.second) }
		}

		val statements = PropertiesService.getPropertiesStatements(connection, diffResult, "itemProperty", "itemID")

		if (itemStatement != null) {
			statements.add(itemStatement)
		}

		return statements
	}
}
