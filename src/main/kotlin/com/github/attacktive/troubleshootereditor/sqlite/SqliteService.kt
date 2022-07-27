package com.github.attacktive.troubleshootereditor.sqlite

import com.github.attacktive.troubleshootereditor.configuration.PropertiesConfiguration
import com.github.attacktive.troubleshootereditor.extension.findById
import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.model.Company
import com.github.attacktive.troubleshootereditor.model.Quest
import com.github.attacktive.troubleshootereditor.model.Roster
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

@Service
class SqliteService(private val propertiesConfiguration: PropertiesConfiguration) {
	private val logger = LoggerFactory.getLogger(SqliteService::class.java)

	fun read(fileName: String): SaveData {
		val file = File(propertiesConfiguration.file.pathToUpload, fileName)
		return read(file)
	}

	fun read(file: File): SaveData {
		val url = "jdbc:sqlite:${file.absolutePath}"
		DriverManager.getConnection(url).use {
			val company = selectCompany(it)
			val quests = selectQuests(it)
			val rosters = selectRosters(it)

			return SaveData(company, quests, rosters)
		}
	}

	private fun selectCompany(connection: Connection): Company {
		val statement = connection.prepareStatement(
			"""
				select
					c.companyID,
					c.CompanyName,
					c.Vill,
					cpm.masterName,
					cp.cpValue
				from company c
					left join companyProperty cp on c.companyID = cp.companyID
					left join companyPropertyMaster cpm on cp.masterIndex = cpm.masterIndex
			""".trimIndent()
		)

		var company: Company? = null
		statement.executeQuery().use {
			while (it.next()) {
				val companyId = it.getLong("companyID")
				val companyName = it.getString("CompanyName")
				val vill = it.getLong("Vill")

				if (company == null) {
					company = Company(companyId, companyName, vill)

					val key = it.getString("masterName")
					val value = it.getString("cpValue")
					company!!.properties[key] = value
				}
			}
		}

		return company!!
	}

	private fun selectQuests(connection: Connection): List<Quest> {
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

	private fun selectRosters(connection: Connection): List<Roster> {
		val statement = connection.prepareStatement(
			"""
				select
					r.rosterID,
					r.rosterName,
					r.rosterClass,
					r.rosterLv,
					r.rosterExp,
					rpm.masterName,
					rp.rpValue
				from roster r
					left join rosterProperty rp on r.rosterID = rp.rosterID
					left join rosterPropertyMaster rpm on rp.masterIndex = rpm.masterIndex
			""".trimIndent()
		)

		val rosters = mutableSetOf<Roster>()
		statement.executeQuery().use {
			while (it.next()) {
				val id = it.getLong("rosterID")
				var roster = rosters.findById(id)
				if (roster == null) {
					val name = it.getString("rosterName")
					val `class` = it.getString("rosterClass")
					val level = it.getLong("rosterLv")
					val exp = it.getLong("rosterExp")
					roster = Roster(id, name, `class`, level, exp)
					rosters.add(roster)
				}

				val propertyName = it.getString("masterName")
				val propertyValue = it.getString("rpValue")

				roster.properties[propertyName] = propertyValue
			}
		}

		return rosters.toList()
	}
}
