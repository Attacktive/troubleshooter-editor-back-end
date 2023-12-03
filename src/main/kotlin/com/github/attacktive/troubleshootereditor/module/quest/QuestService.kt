package com.github.attacktive.troubleshootereditor.module.quest

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.common.extension.findById

object QuestService {
	fun selectQuests(connection: Connection): List<Quest> {
		val statement = connection.prepareStatement(
			"""
				select
					q.questIndex,
					qm.masterName,
					q.questStage,
					qpm.masterName as propertyName,
					qp.qpValue
				from quest q
					left join questMaster qm on q.masterIndex = qm.masterIndex
					left join questProperty qp on q.questIndex = qp.questIndex
					left join questPropertyMaster qpm on qp.qpMasterIndex = qpm.masterIndex
			""".trimIndent()
		)

		val quests = mutableSetOf<Quest>()
		statement.executeQuery().use {
			while (it.next()) {
				val index = it.getLong("questIndex")
				var quest = quests.findById(index)
				if (quest == null) {
					val name = it.getString("masterName")
					val stage = it.getLong("questStage")
					quest = Quest(index, name, stage)
					quests.add(quest)
				}

				val propertyName = it.getString("propertyName")
				val propertyValue = it.getString("qpValue")

				quest.properties[propertyName] = propertyValue
			}
		}

		return quests.toList()
	}

	fun getStatements(diffResult: Quest.DiffResult, connection: Connection): MutableList<PreparedStatement> {
		if (!diffResult.hasChanges) {
			throw RuntimeException("No statements when no changes.")
		}

		var toUpdateQuest = false

		val setClauseBuilder = mutableListOf<Pair<String, Any>>()
		if (diffResult.name != null) {
			toUpdateQuest = true
			TODO("`questMaster` table lacks records as they get inserted as the game progress.")
		}

		if (diffResult.stage != null) {
			setClauseBuilder.add("questStage = ?" to diffResult.stage)
			toUpdateQuest = true
		}

		TODO()
	}
}
