package com.github.attacktive.troubleshootereditor.ingamedata.item.port.outbound

import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.EquipmentPosition
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.Item

interface ItemRepository {
	fun selectItems(url: String): List<Item>
	fun selectEquippedItems(url: String): List<Item>

	fun saveChanges(url: String, newItems: Collection<Item>)

	fun overwriteProperties(url: String, itemsPerPosition: Map<EquipmentPosition?, List<Item>>)
}
