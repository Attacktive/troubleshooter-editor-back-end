package com.github.attacktive.troubleshootereditor.module.common

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.common.diff.DiffResult
import com.github.attacktive.troubleshootereditor.module.Identifiable

object PropertiesService {
	fun <T, TID> getPropertiesStatements(connection: Connection, diffResult: T, tableName: String, idColumnName: String, indexColumnName: String = "masterIndex", valueColumnName: String = "propValue"): MutableList<PreparedStatement> where T: DiffResult, T: Identifiable<TID> {
		val propertyStatements = mutableListOf<PreparedStatement>()

		if (diffResult.properties != null) {
			val properties = MasterService.getProperties(connection, tableName)

			val propertiesDiff = diffResult.properties!!
			propertyStatements.addAll(
				propertiesDiff.inserts.map {
					val masterIndex = properties[it.first]!!

					val statement = connection.prepareStatement(
						"""
						insert into $tableName($idColumnName, $indexColumnName, $valueColumnName)
						values (${diffResult.getId()}, ?, ?)
						""".trimIndent()
					)

					statement.setLong(1, masterIndex)
					statement.setString(2, it.second)

					statement
				}
			)

			propertyStatements.addAll(
				propertiesDiff.updates.map {
					val masterIndex = properties[it.first]!!

					val statement = connection.prepareStatement(
						"""
						update $tableName
						set $valueColumnName = ?
						where $idColumnName = ${diffResult.getId()} and $indexColumnName = ?
						""".trimIndent()
					)

					statement.setString(2, it.second)
					statement.setLong(1, masterIndex)

					statement
				}
			)

			propertyStatements.addAll(
				propertiesDiff.deletes.map {
					val masterIndex = properties[it]!!

					val statement = connection.prepareStatement(
						"""
						delete from $tableName
						where $idColumnName = ${diffResult.getId()} and $indexColumnName = ?
						""".trimIndent()
					)

					statement.setLong(1, masterIndex)

					statement
				}
			)
		}

		return propertyStatements
	}
}
