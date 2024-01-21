package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import java.sql.DriverManager
import com.github.attacktive.troubleshootereditor.domain.common.SaveData
import com.github.attacktive.troubleshootereditor.domain.company.CompanyObject
import com.github.attacktive.troubleshootereditor.domain.item.ItemObject
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
			val rosters = RosterObject.selectRosters(it)

			return SaveData(company, rosters, items)
		}
	}

	fun save(fileName: String, edited: SaveData): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()
		DriverManager.getConnection(url).use { connection ->
			val companyDiffResult = CompanyObject.selectAndDiff(connection, edited.company)
			companyDiffResult.generateStatements(connection).forEach { it.executeUpdate() }

			val itemDiffResult = ItemObject.selectAndDiff(connection, edited.items)
			val rosterDiffResult = RosterObject.selectAndDiff(connection, edited.rosters)

			(itemDiffResult + rosterDiffResult).asSequence()
				.flatMap { it.generateStatements(connection) }
				.forEach { it.executeUpdate() }
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
