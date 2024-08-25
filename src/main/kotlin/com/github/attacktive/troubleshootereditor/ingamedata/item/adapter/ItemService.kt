package com.github.attacktive.troubleshootereditor.ingamedata.item.adapter

import com.github.attacktive.troubleshootereditor.common.extension.findByOrNull
import com.github.attacktive.troubleshootereditor.common.extension.getDiffResults
import com.github.attacktive.troubleshootereditor.common.extension.logger
import com.github.attacktive.troubleshootereditor.common.extension.toProperties
import com.github.attacktive.troubleshootereditor.ingamedata.common.Property
import com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table.ItemEquippedInfos
import com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table.ItemProperties
import com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table.ItemPropertyMaster
import com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table.ItemStatusMaster
import com.github.attacktive.troubleshootereditor.ingamedata.item.adapter.outbound.table.Items
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.EquipmentPosition
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.Item
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.PropertyMaster
import com.github.attacktive.troubleshootereditor.ingamedata.item.port.outbound.ItemRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Service

@Service
class ItemService: ItemRepository {
	private val logger by logger()

	override fun selectItems(url: String): List<Item> {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

			(Items leftJoin ItemStatusMaster leftJoin ItemProperties leftJoin ItemPropertyMaster)
				.select(Items.id, Items.type, Items.count, ItemStatusMaster.name, ItemPropertyMaster.name, ItemProperties.value)
				.where {
					(Items.status eq ItemStatusMaster.index) and (Items.id eq ItemProperties.itemId) and (ItemProperties.masterIndex eq ItemPropertyMaster.index)
				}
				.orderBy(Items.id)
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

	override fun selectEquippedItems(url: String): List<Item> {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

			(Items innerJoin ItemEquippedInfos leftJoin ItemStatusMaster leftJoin ItemProperties leftJoin ItemPropertyMaster)
				.select(Items.id, Items.type, Items.count, ItemStatusMaster.name, ItemPropertyMaster.name, ItemProperties.value, ItemEquippedInfos.positionKey)
				.where {
					(Items.id eq ItemEquippedInfos.itemId) and (Items.status eq ItemStatusMaster.index) and (Items.id eq ItemProperties.itemId) and (ItemProperties.masterIndex eq ItemPropertyMaster.index)
				}
				.orderBy(Items.id)
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

	override fun saveChanges(url: String, newItems: Collection<Item>) {
		Database.connect(url)

		val oldItems = selectItems(url)
		val diffResult = oldItems.getDiffResults(newItems)

		val itemStatusMasterLookup = getItemStatusMasterLookup()
		val itemPropertyMasterLookup = getItemPropertyMasterLookup()

		transaction {
			addLogger(StdOutSqlLogger)

			diffResult.forEach { itemDiff ->
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

				itemDiff.properties.applyPropertyChanges(itemDiff, itemPropertyMasterLookup)
			}
		}
	}

	override fun overwriteProperties(url: String, itemsPerPosition: Map<EquipmentPosition?, List<Item>>) {
		Database.connect(url)

		itemsPerPosition.forEach {
			val position = it.key
			val items = it.value

			if (position == null) {
				logger.warn("An item whose position is ${null} is found.")
			} else {
				val options = position.options
				if (options != null) {
					if (options.size > 5) {
						logger.warn("You can have up to 5 options! Other than the first five are going to be silently ignored.")
					}

					transaction {
						addLogger(StdOutSqlLogger)

						items.forEach { item ->
							ItemProperties.applyDefaultChanges(item.id)

							options.mapIndexed { index, pair ->
								val nthOptions = PropertyMaster.getNthOptions(index + 1)

								ItemProperties.insert(item.id, nthOptions.first, pair.first.value)
								ItemProperties.insert(item.id, nthOptions.second, pair.second)
							}
						}
					}
				}
			}
		}
	}

	private fun getItemStatusMasterLookup() = transaction {
		addLogger(StdOutSqlLogger)

		ItemStatusMaster.select(ItemStatusMaster.index, ItemStatusMaster.name)
			.associate { it[ItemStatusMaster.name] to it[ItemStatusMaster.index] }
	}

	private fun getItemPropertyMasterLookup() = transaction {
		addLogger(StdOutSqlLogger)

		ItemPropertyMaster.select(ItemPropertyMaster.index, ItemPropertyMaster.name)
			.associate { it[ItemPropertyMaster.name] to it[ItemPropertyMaster.index] }
	}
}
