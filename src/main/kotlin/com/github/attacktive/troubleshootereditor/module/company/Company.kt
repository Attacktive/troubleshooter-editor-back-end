package com.github.attacktive.troubleshootereditor.module.company

import java.util.stream.Stream
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.attacktive.troubleshootereditor.common.diff.MapDiffUtils
import com.github.attacktive.troubleshootereditor.common.diff.PropertiesDiffResult
import com.github.attacktive.troubleshootereditor.module.Identifiable
import com.github.attacktive.troubleshootereditor.common.diff.DiffResult as IDiffResult

data class Company(val id: Long, val name: String, val vill: Long): Identifiable<Long> {
	val properties: MutableMap<String, String> = mutableMapOf()

	@JsonIgnore
	override fun getId(): Long {
		return id
	}

	fun diff(desired: Company): DiffResult {
		val name = if (this.name == desired.name) {
			null
		} else {
			desired.name
		}

		val vill = if (this.vill == desired.vill) {
			null
		} else {
			desired.vill
		}

		val properties = if (this.properties == desired.properties) {
			null
		} else {
			MapDiffUtils.diff(this.properties, desired.properties)
		}

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(val id: Long, val name: String?, val vill: Long?, override val properties: PropertiesDiffResult?): IDiffResult, Identifiable<Long> {
		override val hasChanges = Stream.of(name, vill, properties).anyMatch { it != null }

		@JsonIgnore
		override fun getId(): Long {
			return id
		}
	}
}
