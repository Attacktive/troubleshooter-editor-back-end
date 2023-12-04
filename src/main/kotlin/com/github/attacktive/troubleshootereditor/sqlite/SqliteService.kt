package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import com.github.attacktive.troubleshootereditor.common.configuration.PropertiesConfiguration
import com.github.attacktive.troubleshootereditor.module.SaveData
import com.github.attacktive.troubleshootereditor.module.company.CompanyService
import com.github.attacktive.troubleshootereditor.module.item.ItemService
import com.github.attacktive.troubleshootereditor.module.quest.QuestService
import com.github.attacktive.troubleshootereditor.module.roster.RosterService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SqliteService(private val propertiesConfiguration: PropertiesConfiguration) {
	companion object {
		private val logger = LoggerFactory.getLogger(SqliteService::class.java)
	}

	fun read(fileName: String): Connection {
		val file = File(propertiesConfiguration.file.pathToUpload, fileName)
		return read(file)
	}

	fun read(file: File): Connection {
		val url = "jdbc:sqlite:${file.absolutePath}"
		return DriverManager.getConnection(url)
	}

	fun readSaveData(fileName: String): SaveData {
		val file = File(propertiesConfiguration.file.pathToUpload, fileName)
		return readSaveData(file)
	}

	fun readSaveData(file: File): SaveData {
		read(file).use {
			val company = CompanyService.selectCompany(it)
			val quests = QuestService.selectQuests(it)
			val rosters = RosterService.selectRosters(it)
			val items = ItemService.selectItems(it)

			return SaveData(company, quests, rosters, items)
		}
	}

	fun save(sourceSaveData: SaveData, saveData: SaveData) {
		TODO("diff and upsert")
	}
}
