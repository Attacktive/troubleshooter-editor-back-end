package com.github.attacktive.troubleshootereditor.upload

import com.github.attacktive.troubleshootereditor.configuration.PropertiesConfiguration
import com.github.attacktive.troubleshootereditor.model.SaveData
import com.github.attacktive.troubleshootereditor.model.compnay.Company
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.sql.DriverManager

@Service
class SqliteService(private val propertiesConfiguration: PropertiesConfiguration) {
	private val logger = LoggerFactory.getLogger(SqliteService::class.java)

	fun run(fileName: String): SaveData? {
		val file = File(propertiesConfiguration.file.pathToUpload, fileName)
		val url = "jdbc:sqlite:${file.absolutePath}"
		DriverManager.getConnection(url).use { connection ->
			var statement = connection.prepareStatement(
				"""
					select CompanyName, Vill
					from company
					limit 1
				""".trimIndent()
			)

			val saveData: SaveData?
			statement.executeQuery().use {
				val companyName = it.getString("CompanyName")
				val vill = it.getLong("Vill")

				statement = connection.prepareStatement(
					"""
						select
							cpm.masterName,
							cp.cpValue
						from companyPropertyMaster cpm left join companyProperty cp on cp.masterIndex = cpm.masterIndex
					""".trimIndent()
				)

				val properties = mapOf<String, String>()
				statement.executeQuery().use { propertiesResultSet ->
					while (propertiesResultSet.next()) {
						val key = propertiesResultSet.getString("masterName")
						val value = propertiesResultSet.getString("cpValue")
						properties.map { key to value }
					}
				}

				val company = Company(companyName, vill, properties)
				saveData = SaveData(company)
			}

			return saveData
		}
	}
}
