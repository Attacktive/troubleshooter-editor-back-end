package com.github.attacktive.troubleshootereditor.download

import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.sqlite.SqliteService
import org.springframework.stereotype.Service

@Service
class DownloadService(private val sqliteService: SqliteService) {
	fun download(sourceSaveFileName: String, saveData: SaveData) {
		sqliteService.read(sourceSaveFileName).use { connection ->
			val company = sqliteService.selectCompany(connection)
			val companyDiff = company.diff(saveData.company)
			if (companyDiff.hasChanges) {
				val statements = companyDiff.getStatements(connection)
				statements.forEach { it.execute() }
			}

			val quests = sqliteService.selectQuests(connection)
			val rosters = sqliteService.selectRosters(connection)
			val items = sqliteService.selectItems(connection)
		}

		return sqliteService.save(saveData, saveData)
	}
}
