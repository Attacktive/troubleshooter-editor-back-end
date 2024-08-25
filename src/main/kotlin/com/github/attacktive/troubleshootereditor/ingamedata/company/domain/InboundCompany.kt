package com.github.attacktive.troubleshootereditor.ingamedata.company.domain

data class InboundCompany(val id: Int, val name: String, val vill: Long, val properties: Map<String, String>) {
	fun toCompany() = Company(id, name, vill, properties)
}
