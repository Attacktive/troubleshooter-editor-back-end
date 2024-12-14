package com.github.attacktive.troubleshootereditor.sqlite

import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import com.github.attacktive.troubleshootereditor.ingamedata.common.InboundSaveData
import com.github.attacktive.troubleshootereditor.ingamedata.company.domain.InboundCompany
import com.github.attacktive.troubleshootereditor.ingamedata.item.domain.InboundItem
import com.github.attacktive.troubleshootereditor.ingamedata.roster.domain.InboundRoster
import com.github.attacktive.troubleshootereditor.ingamedata.common.Properties
import com.github.attacktive.troubleshootereditor.sqlite.port.inbound.SqliteUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SqliteServiceTest(@Autowired private val sqliteUseCase: SqliteUseCase) {
	@Test
	fun testReadFile() {
		val url = SqliteServiceTest::class.java.classLoader.getResource("game.sav")
		val file = File(url!!.toURI())

		val saveData = sqliteUseCase.readFile(file)
		assertNotNull(saveData)

		println(saveData)
	}

	@Test
	fun testSave() {
		val inboundCompany = InboundCompany(
			id = 1,
			name = "Test Company",
			vill = 1000L,
			properties = mapOf("GameDifficulty" to "Hard")
		)

		val inboundRoster = InboundRoster(
			id = 1L,
			name = "Albus: renamed",
			`class` = "Albus",
			level = 5L,
			exp = 1000L,
			properties = Properties(mapOf("BasicMastery" to "HungryWolf", "SalaryDuration" to "7"))
		)

		val inboundItem = InboundItem(
			id = 10L,
			type = "Weapon",
			count = 1L,
			status = "equipped",
			properties = mapOf("Option/Type1" to "AttackPower", "Option/Value1" to "666")
		)

		val inboundSaveData = InboundSaveData(
			company = inboundCompany,
			rosters = listOf(inboundRoster),
			items = listOf(inboundItem)
		)

		val tempFile = File.createTempFile("test_save", ".sav")

		val url = SqliteServiceTest::class.java.classLoader.getResource("game.sav")
		val inputFile = File(url!!.toURI())
		inputFile.copyTo(tempFile, true)

		try {
			sqliteUseCase.save(tempFile.name, inboundSaveData)

			val editedData = sqliteUseCase.readFileByName(tempFile.name)

			val editedCompany = editedData.company
			assertEquals("Test Company", editedCompany.name)
			assertEquals(1000L, editedCompany.vill)
			assertEquals("Hard", editedCompany.properties.toMap()["GameDifficulty"])

			val editedRoster = editedData.rosters.find { it.id == inboundRoster.id }
			assertNotNull(editedRoster)
			assertEquals("Albus: renamed", editedRoster.name)
			assertEquals("Albus", editedRoster.`class`)
			assertEquals(5L, editedRoster.level)
			assertEquals(1000L, editedRoster.exp)
			assertEquals("HungryWolf", editedRoster.properties.toMap()["BasicMastery"])
			assertEquals("7", editedRoster.properties.toMap()["SalaryDuration"])

			val editedItem = editedData.items.find { it.id == inboundItem.id }
			assertNotNull(editedItem)
			assertEquals("Weapon", editedItem.type)
			assertEquals(1L, editedItem.count)
			assertEquals("equipped", editedItem.status)
			assertEquals("AttackPower", editedItem.properties.toMap()["Option/Type1"])
			assertEquals("666", editedItem.properties.toMap()["Option/Value1"])
		} finally {
			tempFile.delete()
		}
	}
}
