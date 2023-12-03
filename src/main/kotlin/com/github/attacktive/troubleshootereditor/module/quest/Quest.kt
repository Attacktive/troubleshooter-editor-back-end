package com.github.attacktive.troubleshootereditor.module.quest

import java.util.stream.Stream
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.attacktive.troubleshootereditor.common.diff.PropertiesDiffResult
import com.github.attacktive.troubleshootereditor.module.Identifiable
import com.github.attacktive.troubleshootereditor.common.diff.DiffResult as IDiffResult

data class Quest(val index: Long, val name: String, val stage: Long): Identifiable<Long> {
	val properties: MutableMap<String, String> = mutableMapOf()

	@JsonIgnore
	override fun getId(): Long {
		return index
	}

	fun diff(desired: Quest): DiffResult {
		val name = if (this.name == desired.name) {
			null
		} else {
			desired.name
		}

		val stage = if (this.stage == desired.stage) {
			null
		} else {
			desired.stage
		}

		return DiffResult(index, name, stage)
	}

	data class DiffResult(val index: Long, val name: String?, val stage: Long?, override val properties: PropertiesDiffResult? = null): IDiffResult, Identifiable<Long> {
		override val hasChanges = Stream.of(name, stage).anyMatch { it != null }
		@JsonIgnore
		override fun getId(): Long {
			return index
		}
	}
}
