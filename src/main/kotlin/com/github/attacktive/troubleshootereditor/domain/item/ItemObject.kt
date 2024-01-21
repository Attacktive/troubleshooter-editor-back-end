package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.extension.findById
import com.github.attacktive.troubleshootereditor.extension.getDiffResults
import com.github.attacktive.troubleshootereditor.extension.logger

object ItemObject {
	private val logger by logger()

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

		return getItemsFromStatement(statement)
	}

	fun selectEquippedItems(connection: Connection): List<Item> {
		val statement = connection.prepareStatement(
			"""
				select
					i.itemID,
					i.itemType,
					i.itemCount,
					ism.masterName,
					iif(ipm.masterIndex is null, '{}', json_group_object(ipm.masterName, ip.propValue)) as properties,
					ie.positionKey
				from item i
					join itemEquippedInfo ie on i.itemID = ie.itemID
					left join itemStatusMaster ism on i.itemStatus = ism.masterIndex
					left join itemProperty ip on i.itemID = ip.itemID
					left join itemPropertyMaster ipm on ip.masterIndex = ipm.masterIndex
				group by i.itemID, i.itemType, i.itemCount, ism.masterName, ie.positionKey
			""".trimIndent()
		)

		return getItemsFromStatement(statement)
	}

	fun selectAndDiff(connection: Connection, newItems: Collection<Item>): List<Item.DiffResult> {
		val oldItems = selectItems(connection)
		return oldItems.getDiffResults(newItems)
	}

	fun overwriteProperties(connection: Connection, itemsPerPosition: Map<EquipmentPosition?, List<Item>>) {
		itemsPerPosition.map {
			val position = it.key!!
			val items = it.value

			val cheatingFunction = position.cheatingStatements(connection)

			items.map { item -> cheatingFunction(item.id) }
				.flatten()
		}
			.flatten()
			.onEach { statement -> logger.debug(statement.toString()) }
			.forEach { statement -> statement.executeUpdate() }
	}

	private fun getItemsFromStatement(statement: PreparedStatement): List<Item> {
		val items = mutableListOf<Item>()
		statement.executeQuery().use {
			while (it.next()) {
				val item = Item.fromResultSet(it)
				items.add(item)
			}
		}

		return items
	}
}
