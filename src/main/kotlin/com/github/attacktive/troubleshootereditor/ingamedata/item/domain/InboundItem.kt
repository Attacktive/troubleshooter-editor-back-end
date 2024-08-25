package com.github.attacktive.troubleshootereditor.ingamedata.item.domain

data class InboundItem(val id: Long, val type: String, val count: Long, val status: String, val properties: Map<String, String>) {
	fun toItem() = Item(id, type, count, status, properties)
}
