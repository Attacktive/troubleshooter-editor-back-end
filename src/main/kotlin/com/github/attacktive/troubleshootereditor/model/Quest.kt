package com.github.attacktive.troubleshootereditor.model

import java.sql.Connection
import java.sql.PreparedStatement
import java.util.stream.Stream
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.attacktive.troubleshootereditor.model.DiffResult as IDiffResult

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

	data class DiffResult(private val index: Long, val name: String?, val stage: Long?): IDiffResult {
		override val hasChanges = Stream.of(name, stage).anyMatch { it != null }

		override fun getStatements(connection: Connection): MutableList<PreparedStatement> {
			if (!hasChanges) {
				throw RuntimeException("No statements when no changes.")
			}

			var toUpdateQuest = false

			val setClauseBuilder = mutableListOf<Pair<String, Any>>()
			if (name != null) {
				toUpdateQuest = true
				TODO("`questMaster` table lacks records as they get inserted as the game progress.")
			}

			if (stage != null) {
				setClauseBuilder.add("questStage = ?" to stage)
				toUpdateQuest = true
			}

			TODO()
		}
	}
}
