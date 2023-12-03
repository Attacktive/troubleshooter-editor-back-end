package com.github.attacktive.troubleshootereditor.download

import com.github.attacktive.troubleshootereditor.module.SaveData
import com.github.attacktive.troubleshootereditor.module.company.CompanyService
import com.github.attacktive.troubleshootereditor.module.item.ItemService
import com.github.attacktive.troubleshootereditor.module.quest.QuestService
import com.github.attacktive.troubleshootereditor.module.roster.RosterService
import com.github.attacktive.troubleshootereditor.sqlite.SqliteService
import org.springframework.stereotype.Service

@Service
class DownloadService(private val sqliteService: SqliteService) {
	fun download(sourceSaveFileName: String, saveData: SaveData) {
		sqliteService.read(sourceSaveFileName).use { connection ->
			val company = CompanyService.selectCompany(connection)
			val companyDiff = company.diff(saveData.company)
			if (companyDiff.hasChanges) {
				val statements = CompanyService.getStatements(companyDiff, connection)
				statements.forEach { it.execute() }
			}

			val quests = QuestService.selectQuests(connection)
			val rosters = RosterService.selectRosters(connection)
			val items = ItemService.selectItems(connection)
		}

		return sqliteService.save(saveData, saveData)
	}
}
