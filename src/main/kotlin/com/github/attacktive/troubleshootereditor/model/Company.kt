package com.github.attacktive.troubleshootereditor.model

import java.sql.Connection
import java.sql.PreparedStatement
import java.util.stream.Stream
import com.github.attacktive.troubleshootereditor.diff.MapDiffUtils
import com.github.attacktive.troubleshootereditor.model.DiffResult as IDiffResult

data class Company(val id: Long, val name: String, val vill: Long) {
	val properties: MutableMap<String, String> = mutableMapOf()

	fun diff(desired: Company): DiffResult {
		val name = if (this.name == desired.name) {
			null
		} else {
			desired.name
		}

		val vill = if (this.vill == desired.vill) {
			null
		} else {
			desired.vill
		}

		val properties = if (this.properties == desired.properties) {
			null
		} else {
			MapDiffUtils.diff(this.properties, desired.properties)
		}

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(private val id: Long, val name: String?, val vill: Long?, val properties: PropertiesDiffResult?): IDiffResult {
		override val hasChanges = Stream.of(name, vill, properties).anyMatch { it != null }

		fun getStatements(connection: Connection): Boolean {
			if (!hasChanges) {
				throw RuntimeException("No statements when no changes.")
			}

			val companyProperties = mutableMapOf<String, Long>()
			val preparedStatement = connection.prepareStatement("select masterIndex, masterName from companyPropertyMaster")
			preparedStatement.executeQuery().use {
				while (it.next()) {
					val propertyId = it.getLong("masterIndex")
					val propertyName = it.getString("masterName")

					companyProperties[propertyName] = propertyId
				}
			}

			var toUpdateCompany = false

			val setClauseBuilder = mutableListOf<Pair<String, Any>>()
			if (name != null) {
				setClauseBuilder.add("CompanyName = ?" to name)
				toUpdateCompany = true
			}

			if (vill != null) {
				setClauseBuilder.add("Vill = ?" to vill)
				toUpdateCompany = true
			}

			var companyStatement: PreparedStatement? = null
			if (toUpdateCompany) {
				val setClause = setClauseBuilder.joinToString(", ") { it.first }

				val companyQuery = "update company $setClause where companyID = $id"
				companyStatement = connection.prepareStatement(companyQuery)

				setClauseBuilder.forEachIndexed { index, pair -> companyStatement.setObject((index + 1), pair.second) }
			}

			val propertyStatements = mutableListOf<PreparedStatement>()
			if (properties != null) {
				propertyStatements.addAll(
					properties.inserts.map {
						val masterIndex = companyProperties[it.first]!!

						val statement = connection.prepareStatement("insert into companyProperty(companyID, masterIndex, cpValue) values ($id, ?, ?)")
						statement.setLong(1, masterIndex)
						statement.setString(2, it.second)

						statement
					}
				)

				propertyStatements.addAll(
					properties.updates.map {
						val masterIndex = companyProperties[it.first]!!

						val statement = connection.prepareStatement("update companyProperty set cpValue = ? where companyID = $id and masterIndex = ?")
						statement.setString(1, it.second)
						statement.setLong(2, masterIndex)

						statement
					}
				)

				propertyStatements.addAll(
					properties.deletes.map {
						val masterIndex = companyProperties[it]!!

						val statement = connection.prepareStatement("delete from companyProperty where companyID = $id and masterIndex = ?")
						statement.setLong(1, masterIndex)

						statement
					}
				)
			}

			return mutableListOf(companyStatement)
				.filterNotNull()
				.toMutableList()
				.addAll(propertyStatements)
		}
	}
}
