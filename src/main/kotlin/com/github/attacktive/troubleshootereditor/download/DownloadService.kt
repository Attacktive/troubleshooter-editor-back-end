package com.github.attacktive.troubleshootereditor.download

import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.sqlite.SqliteService
import org.springframework.stereotype.Service

@Service
class DownloadService(private val sqliteService: SqliteService) {
	fun download(sourceSaveFileName: String, saveData: SaveData) {
		sqliteService.read(sourceSaveFileName).use {
			val company = sqliteService.selectCompany(it)
			val companyDiff = company.diff(saveData.company)
			if (companyDiff.hasChanges) {
				//companyDiff.statements
				//it.
			}

			val quests = sqliteService.selectQuests(it)
			val rosters = sqliteService.selectRosters(it)
			val items = sqliteService.selectItems(it)
		}

		return sqliteService.save(saveData, saveData)
		//return sqliteService.save(sourceSaveFileName, saveData)
	}
}
