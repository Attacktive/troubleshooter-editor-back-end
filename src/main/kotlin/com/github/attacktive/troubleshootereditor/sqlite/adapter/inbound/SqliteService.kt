package com.github.attacktive.troubleshootereditor.sqlite.adapter.inbound

import java.io.File
import com.github.attacktive.troubleshootereditor.ingamedata.common.InboundSaveData
import com.github.attacktive.troubleshootereditor.ingamedata.common.SaveData
import com.github.attacktive.troubleshootereditor.ingamedata.company.port.outbound.CompanyRepository
import com.github.attacktive.troubleshootereditor.ingamedata.item.port.outbound.ItemRepository
import com.github.attacktive.troubleshootereditor.ingamedata.roster.port.outbound.RosterRepository
import com.github.attacktive.troubleshootereditor.sqlite.port.inbound.SqliteUseCase
import org.springframework.stereotype.Service

@Service
class SqliteService (private val companyRepository: CompanyRepository, private val itemRepository: ItemRepository, private val rosterRepository: RosterRepository): SqliteUseCase {
	companion object {
		private val tmpdir = System.getProperty("java.io.tmpdir")
	}

	override fun readFileByName(fileName: String): SaveData {
		val file = File(tmpdir, fileName)
		return readFile(file)
	}

	override fun readFile(file: File): SaveData {
		val url = file.getJdbcUrl()
		val company = companyRepository.selectCompany(url)
		val items = itemRepository.selectItems(url)
		val rosters = rosterRepository.selectRosters(url)

		return SaveData(company, rosters, items)
	}

	override fun save(fileName: String, inboundSaveData: InboundSaveData): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()

		companyRepository.saveChanges(url, inboundSaveData.company.toCompany())
		itemRepository.saveChanges(url, inboundSaveData.items.map { it.toItem() })
		rosterRepository.saveChanges(url, inboundSaveData.rosters.map { it.toRoster() })

		return file.absolutePath
	}

	override fun applyQuickCheats(fileName: String): String {
		val file = File(tmpdir, fileName)
		val url = file.getJdbcUrl()

		val equippedItemsByPosition = itemRepository.selectEquippedItems(url)
			.filterNot { it.equipmentPosition == null }
			.groupBy { it.equipmentPosition }

		itemRepository.overwriteProperties(url, equippedItemsByPosition)

		return file.absolutePath
	}
}

private fun File.getJdbcUrl() = "jdbc:sqlite:${absolutePath}"
