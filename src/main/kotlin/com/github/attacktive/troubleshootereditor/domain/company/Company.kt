package com.github.attacktive.troubleshootereditor.domain.company

import com.fasterxml.jackson.annotation.JsonGetter
import com.github.attacktive.troubleshootereditor.domain.common.Properties

data class Company(val id: Int, val name: String, val vill: Long, val properties: Properties) {
	constructor(id: Int, name: String, vill:Long, properties: Map<String, String>): this(id, name, vill, Properties(properties))

	@JsonGetter(value = "properties")
	fun properties() = properties.toMap().toSortedMap()

	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(val id: Int, val name: String?, val vill: Long?, val properties: Properties)
}
