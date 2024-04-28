package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import com.github.attacktive.troubleshootereditor.domain.common.DiffType
import com.github.attacktive.troubleshootereditor.domain.common.Property
import com.github.attacktive.troubleshootereditor.domain.item.table.ItemEquippedInfos
import com.github.attacktive.troubleshootereditor.domain.item.table.ItemProperties
import com.github.attacktive.troubleshootereditor.domain.item.table.ItemPropertyMaster
import com.github.attacktive.troubleshootereditor.domain.item.table.ItemStatusMaster
import com.github.attacktive.troubleshootereditor.domain.item.table.Items
import com.github.attacktive.troubleshootereditor.extension.findByOrNull
import com.github.attacktive.troubleshootereditor.extension.getDiffResults
import com.github.attacktive.troubleshootereditor.extension.logger
import com.github.attacktive.troubleshootereditor.extension.toProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object ItemObject {
	private val logger by logger()

	fun selectItems(url: String): List<Item> {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

			(Items leftJoin ItemStatusMaster leftJoin ItemProperties leftJoin ItemPropertyMaster)
				.select(Items.id, Items.type, Items.count, ItemStatusMaster.name, ItemPropertyMaster.name, ItemProperties.value)
				.where {
					(Items.status eq ItemStatusMaster.index) and (Items.id eq ItemProperties.itemId) and (ItemProperties.masterIndex eq ItemPropertyMaster.index)
				}
				.orderBy(Items.id)
				.asSequence()
				.groupBy { it[Items.id] }
				.values
				.map { resultRows ->
					val properties = resultRows.map { resultRow -> resultRow[ItemPropertyMaster.name] to resultRow[ItemProperties.value] }
						.map { Property(it) }
						.toMutableList()
						.toProperties()

					val resultRow = resultRows[0]
					Item(resultRow[Items.id], resultRow[Items.type], resultRow[Items.count], resultRow[ItemStatusMaster.name], properties)
				}
		}
	}

	fun selectEquippedItems(url: String): List<Item> {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

			(Items innerJoin ItemEquippedInfos leftJoin ItemStatusMaster leftJoin ItemProperties leftJoin ItemPropertyMaster)
				.select(Items.id, Items.type, Items.count, ItemStatusMaster.name, ItemPropertyMaster.name, ItemProperties.value, ItemEquippedInfos.positionKey)
				.where {
					(Items.id eq ItemEquippedInfos.itemId) and (Items.status eq ItemStatusMaster.index) and (Items.id eq ItemProperties.itemId) and (ItemProperties.masterIndex eq ItemPropertyMaster.index)
				}
				.orderBy(Items.id)
				.asSequence()
				.groupBy { it[Items.id] }
				.values
				.map { resultRows ->
					val properties = resultRows.map { resultRow -> resultRow[ItemPropertyMaster.name] to resultRow[ItemProperties.value] }
						.map { Property(it) }
						.toMutableList()
						.toProperties()

					val resultRow = resultRows[0]

					val positionKey = resultRow[ItemEquippedInfos.positionKey]
					val equipmentPosition: EquipmentPosition? = EquipmentPosition::value findByOrNull positionKey

					Item(resultRow[Items.id], resultRow[Items.type], resultRow[Items.count], resultRow[ItemStatusMaster.name], properties, equipmentPosition)
				}
		}
	}

	fun saveChanges(url: String, newItems: Collection<Item>) {
		val oldItems = selectItems(url)
		val diffResult = oldItems.getDiffResults(newItems)

		val itemStatusMasterLookup = transaction {
			addLogger(StdOutSqlLogger)

			ItemStatusMaster.select(ItemStatusMaster.index, ItemStatusMaster.name)
				.map { it[ItemStatusMaster.name] to it[ItemStatusMaster.index] }
				.toMap()
		}

		val itemPropertyMasterLookup = transaction {
			addLogger(StdOutSqlLogger)

			ItemPropertyMaster.select(ItemPropertyMaster.index, ItemPropertyMaster.name)
				.map { it[ItemPropertyMaster.name] to it[ItemPropertyMaster.index] }
				.toMap()
		}

		transaction {
			addLogger(StdOutSqlLogger)

			diffResult.asSequence()
				.forEach { itemDiff ->
					if (itemDiff.type != null) {
						Items.update({ Items.id eq itemDiff.id }) {
							it[type] = itemDiff.type
						}
					}

					if (itemDiff.count != null) {
						Items.update({ Items.id eq itemDiff.id }) {
							it[count] = itemDiff.count
						}
					}

					if (itemDiff.status != null) {
						val statusIndex = itemStatusMasterLookup[itemDiff.status]
						if (statusIndex == null) {
							logger.warn("Failed to find item status master index for \"${itemDiff.status}\"; ignoring. 😞")
						} else {
							Items.update({ Items.id eq itemDiff.id }) {
								it[status] = statusIndex
							}
						}
					}

					itemDiff.properties
						.asSequence()
						.forEach { property ->
							val propertyIndex = itemPropertyMasterLookup[property.key]
							if (propertyIndex == null) {
								logger.warn("Failed to find item property master index for \"${property.key}\"; ignoring. 😞")
							} else {
								when (property.diffType) {
									DiffType.NONE -> {}
									DiffType.ADDED -> {
										ItemProperties.insert {
											it[itemId] = itemDiff.id
											it[masterIndex] = propertyIndex
											it[value] = property.value
										}
									}
									DiffType.MODIFIED -> {
										ItemProperties.update({ (ItemProperties.itemId eq itemDiff.id) and (ItemProperties.masterIndex eq propertyIndex) }) {
											it[value] = property.value
										}
									}
									DiffType.REMOVED -> {
										ItemProperties.deleteWhere {
											(itemId eq itemDiff.id) and (masterIndex eq propertyIndex)
										}
									}
								}
							}
						}
				}
		}
	}

	fun overwriteProperties(connection: Connection, itemsPerPosition: Map<EquipmentPosition?, List<Item>>) {
		itemsPerPosition
			.map {
				val position = it.key!!
				val items = it.value

				val cheatingFunction = position.cheatingStatements(connection)

				items.map { item -> cheatingFunction(item.id) }.flatten()
			}
			.flatten()
			.onEach { statement -> logger.debug(statement.toString()) }
			.forEach { statement -> statement.executeUpdate() }
	}
}
