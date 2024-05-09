package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import com.github.attacktive.troubleshootereditor.domain.common.InboundSaveData
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
		val company = CompanyObject.selectCompany(url)
		val items = ItemObject.selectItems(url)
		val rosters = RosterObject.selectRosters(url)

		return SaveData(company, rosters, items)
	}

	fun save(fileName: String, inboundSaveData: InboundSaveData): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()

		CompanyObject.saveChanges(url, inboundSaveData.company.toCompany())
		ItemObject.saveChanges(url, inboundSaveData.items.map { it.toItem() })
		RosterObject.saveChanges(url, inboundSaveData.rosters.map { it.toRoster() })

		return file.absolutePath
	}

	fun applyQuickCheats(fileName: String): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()

		val equippedItemsByPosition = ItemObject.selectEquippedItems(url)
			.filterNot { it.equipmentPosition == null }
			.groupBy { it.equipmentPosition }

		ItemObject.overwriteProperties(url, equippedItemsByPosition)

		return file.absolutePath
	}
}
