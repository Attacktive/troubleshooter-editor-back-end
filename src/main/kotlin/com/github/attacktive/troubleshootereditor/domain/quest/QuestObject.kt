package com.github.attacktive.troubleshootereditor.domain.quest

import java.sql.Connection
import com.github.attacktive.troubleshootereditor.extension.findById

object QuestObject {
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

		val quests = mutableListOf<Quest>()
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

		return quests
	}
}
