package com.github.attacktive.troubleshootereditor.module.common

import java.sql.Connection

object MasterService {
	fun getProperties(connection: Connection, tableName: String, indexColumnName: String = "masterIndex", nameColumnName: String = "masterName"): MutableMap<String, Long> {
		val properties = mutableMapOf<String, Long>()

		val preparedStatement = connection.prepareStatement(
			"""
			select $indexColumnName, $nameColumnName
			from $tableName
			""".trimIndent()
		)

		preparedStatement.executeQuery().use {
			while (it.next()) {
				val propertyId = it.getLong("masterIndex")
				val propertyName = it.getString("masterName")

				properties[propertyName] = propertyId
			}
		}

		return properties
	}
}
