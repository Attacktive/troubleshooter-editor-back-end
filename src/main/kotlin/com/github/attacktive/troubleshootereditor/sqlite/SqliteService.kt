package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import java.sql.DriverManager
import com.github.attacktive.troubleshootereditor.domain.common.SaveData
import com.github.attacktive.troubleshootereditor.domain.company.CompanyObject
import com.github.attacktive.troubleshootereditor.domain.item.ItemObject
import com.github.attacktive.troubleshootereditor.domain.quest.QuestObject
import com.github.attacktive.troubleshootereditor.domain.roster.RosterObject
import com.github.attacktive.troubleshootereditor.extension.getJdbcUrl
import org.springframework.stereotype.Service

@Service
class SqliteService {
	companion object {
		private val tmpdir = System.getProperty("java.io.tmpdir")
	}

	fun read(fileName: String): SaveData {
		val file = File(tmpdir, fileName)
		return read(file)
	}

	fun read(file: File): SaveData {
		val url = file.getJdbcUrl()
		DriverManager.getConnection(url).use {
			val company = CompanyObject.selectCompany(it)
			val items = ItemObject.selectItems(it)
			val quests = QuestObject.selectQuests(it)
			val rosters = RosterObject.selectRosters(it)

			return SaveData(company, quests, rosters, items)
		}
	}

	fun save(fileName: String, edited: SaveData): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()
		DriverManager.getConnection(url).use { connection ->
			val oldCompany = CompanyObject.selectCompany(connection)
			val newCompany = edited.company

			val diffResult = oldCompany.diff(newCompany)
			diffResult.generateStatements(connection).forEach { it.executeUpdate() }
		}

		return file.absolutePath
	}

	fun applyQuickCheats(fileName: String): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()
		DriverManager.getConnection(url).use { connection ->
			val equippedItemsByPosition = ItemObject.selectEquippedItems(connection)
				.filterNot { it.equipmentPosition == null }
				.groupBy { it.equipmentPosition }

			ItemObject.overwriteProperties(connection, equippedItemsByPosition)
		}

		return file.absolutePath
	}
}
