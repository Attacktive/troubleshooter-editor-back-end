package com.github.attacktive.troubleshootereditor.module.common

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.common.diff.DiffResult
import com.github.attacktive.troubleshootereditor.module.Identifiable

object PropertiesService {
	fun <T, TID> getPropertiesStatements(connection: Connection, diffResult: T, masterTableDetails: MasterTableDetails): MutableList<PreparedStatement> where T: DiffResult, T: Identifiable<TID> {
		val propertyStatements = mutableListOf<PreparedStatement>()

		if (diffResult.properties != null) {
			val properties = MasterService.getProperties(connection, masterTableDetails.tableName)

			val propertiesDiff = diffResult.properties!!
			propertyStatements.addAll(
				propertiesDiff.inserts.map {
					val masterIndex = properties[it.first]!!

					val statement = connection.prepareStatement(
						"""
						insert into ${masterTableDetails.tableName}(${masterTableDetails.idColumnName}, ${masterTableDetails.indexColumnName}, ${masterTableDetails.valueColumnName})
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
						update ${masterTableDetails.tableName}
						set ${masterTableDetails.valueColumnName} = ?
						where ${masterTableDetails.idColumnName} = ${diffResult.getId()} and ${masterTableDetails.indexColumnName} = ?
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
						delete from ${masterTableDetails.tableName}
						where ${masterTableDetails.idColumnName} = ${diffResult.getId()} and ${masterTableDetails.indexColumnName} = ?
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
