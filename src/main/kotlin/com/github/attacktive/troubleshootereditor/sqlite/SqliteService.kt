package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import com.github.attacktive.troubleshootereditor.extension.findById
import com.github.attacktive.troubleshootereditor.model.Company
import com.github.attacktive.troubleshootereditor.model.Item
import com.github.attacktive.troubleshootereditor.model.Quest
import com.github.attacktive.troubleshootereditor.model.Roster
import com.github.attacktive.troubleshootereditor.model.SaveData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SqliteService {
	companion object {
		private val logger = LoggerFactory.getLogger(SqliteService::class.java)
		private val tmpdir = System.getProperty("java.io.tmpdir")
	}

	fun read(fileName: String): SaveData {
		val file = File(tmpdir, fileName)
		return read(file)
	}

	fun read(file: File): SaveData {
		val url = "jdbc:sqlite:${file.absolutePath}"
		DriverManager.getConnection(url).use {
			val company = selectCompany(it)
			val quests = selectQuests(it)
			val rosters = selectRosters(it)
			val items = selectItems(it)

			return SaveData(company, quests, rosters, items)
		}
	}

	fun applyQuickCheats(fileName: String): String {
		val file = File(tmpdir, fileName)
		val url = "jdbc:sqlite:${file.absolutePath}"
		DriverManager.getConnection(url).use { connection ->
			val equippedItemsByPosition = selectEquippedItems(connection)
				.filterNot { it.equipmentPosition == null }
				.groupBy { it.equipmentPosition }

			overwriteProperties(connection, equippedItemsByPosition)
		}

		return file.absolutePath
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

		val rosters = mutableListOf<Roster>()
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

		return rosters
	}

	private fun selectItems(connection: Connection): List<Item> {
		val statement = connection.prepareStatement(
			"""
				select
					i.itemID,
					i.itemType,
					i.itemCount,
					ism.masterName,
					iif(ipm.masterIndex is null, '{}', json_group_object(ipm.masterName, ip.propValue)) as properties
				from item i
					left join itemStatusMaster ism on i.itemStatus = ism.masterIndex
					left join itemProperty ip on i.itemID = ip.itemID
					left join itemPropertyMaster ipm on ip.masterIndex = ipm.masterIndex
				group by i.itemID, i.itemType, i.itemCount, ism.masterName
			""".trimIndent()
		)

		val items = mutableListOf<Item>()
		statement.executeQuery().use {
			while (it.next()) {
				val item = Item.fromResultSet(it)
				items.add(item)
			}
		}

		return items
	}

	private fun selectEquippedItems(connection: Connection): List<Item> {
		val statement = connection.prepareStatement(
			"""
				select
					i.itemID,
					i.itemType,
					i.itemCount,
					ism.masterName,
					iif(ipm.masterIndex is null, '{}', json_group_object(ipm.masterName, ip.propValue)) as properties,
					ie.positionKey
				from item i
					join itemEquippedInfo ie on i.itemID = ie.itemID
					left join itemStatusMaster ism on i.itemStatus = ism.masterIndex
					left join itemProperty ip on i.itemID = ip.itemID
					left join itemPropertyMaster ipm on ip.masterIndex = ipm.masterIndex
				group by i.itemID, i.itemType, i.itemCount, ism.masterName
			""".trimIndent()
		)

		val items = mutableListOf<Item>()
		statement.executeQuery().use {
			while (it.next()) {
				val item = Item.fromResultSet(it)
				items.add(item)
			}
		}

		return items
	}

	private fun overwriteProperties(connection: Connection, itemsPerPosition: Map<Item.EquipmentPosition?, List<Item>>) {
		itemsPerPosition.map {
			val position = it.key!!
			val items = it.value

			val cheatingFunction = position.cheatingStatements(connection)

			items.map { item -> cheatingFunction(item.id) }
				.flatten()
		}
		.flatten()
		.onEach { statement -> logger.debug(statement.toString()) }
		.forEach { statement -> statement.executeUpdate() }
	}
}
