package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.domain.DiffType
import com.github.attacktive.troubleshootereditor.domain.Properties

data class Company(val id: Int, val name: String, val vill: Long, val properties: Properties = Properties()) {
	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(val id: Int, val name: String?, val vill: Long?, val properties: Properties) {
		fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: List<PreparedStatement> = mutableListOf()

			if (name != null) {
				val statement = connection.prepareStatement("""
					update main.company
					set CompanyName = ?
					where companyID = ?
				""".trimIndent())
				statement.setString(1, name)
				statement.setInt(2, id)

				statements.addLast(statement)
			}

			if (vill != null) {
				val statement = connection.prepareStatement("""
					update main.company
					set Vill = ?
					where companyID = ?
				""".trimIndent())
				statement.setLong(1, vill)
				statement.setInt(2, id)

				statements.addLast(statement)
			}

			if (properties.isNotEmpty()) {
				properties.map { property ->
					when (property.diffType) {
						DiffType.NONE -> null
						DiffType.ADDED -> {
							connection.prepareStatement(
								"""
									insert into companyProperty (companyId, masterIndex, cpValue)
									select
										$id,
										cpm.masterIndex,
										'${property.value}'
									from companyPropertyMaster cpm
									where masterName = '${property.value}'
								""".trimIndent()
							)
						}
						DiffType.REMOVED -> {
							connection.prepareStatement(
								"""
									delete from companyProperty
									where masterIndex = (
										select masterIndex
										from companyPropertyMaster
										where companyID = $id and masterName = '${property.key}'
									)
								""".trimIndent()
							)
						}
						DiffType.MODIFIED -> {
							connection.prepareStatement(
								"""
									update companyProperty
									set cpValue = '${property.value}'
									where companyID = $id and masterIndex = (
										select masterIndex
										from companyPropertyMaster
										where masterName = '$property.key'
									)
								""".trimIndent()
							)
						}
					}
				}
				.forEach { statements.addLast(it) }
			}

			return statements
		}
	}
}
