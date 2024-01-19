package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonSetter
import com.github.attacktive.troubleshootereditor.domain.common.DiffType
import com.github.attacktive.troubleshootereditor.domain.common.Properties

data class Company(val id: Int, val name: String, val vill: Long) {
	val properties: Properties = Properties()

	fun addProperty(property: Pair<String, String>) = properties.add(property)

	@JsonGetter(value = "properties")
	fun properties() = properties.toMap()

	@JsonSetter(value = "properties")
	fun properties(properties: Map<String, String>) = addProperties(properties)

	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	private fun addProperties(properties: Map<String, String>) = properties.entries.forEach { addProperty(it.toPair()) }

	data class DiffResult(val id: Int, val name: String?, val vill: Long?, val properties: Properties) {
		fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: List<PreparedStatement> = mutableListOf()

			if (name != null) {
				statements.addLast(updateStatementForName(connection))
			}

			if (vill != null) {
				statements.addLast(updateStatementForVill(connection))
			}

			properties.asSequence().map { property ->
				when (property.diffType) {
					DiffType.NONE -> null
					DiffType.ADDED -> insertStatementForProperty(connection, property.key, property.value)
					DiffType.MODIFIED -> updateStatementForProperty(connection, property.key, property.value)
					DiffType.REMOVED -> deleteStatementForProperty(connection, property.key)
				}
			}
			.forEach { statements.addLast(it) }

			return statements
		}

		private fun updateStatementForName(connection: Connection) = connection.prepareStatement("""
				update company
				set CompanyName = '$name'
				where companyID = $id
			""".trimIndent()
		)

		private fun updateStatementForVill(connection: Connection) = connection.prepareStatement("""
				update company
				set Vill = $vill
				where companyID = $id
			""".trimIndent()
		)

		private fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String) = connection.prepareStatement("""
				insert into companyProperty (companyId, masterIndex, cpValue)
				select
					$id,
					cpm.masterIndex,
					'$propertyValue'
				from companyPropertyMaster cpm
				where masterName = '$propertyName'
			""".trimIndent()
		)

		private fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String) = connection.prepareStatement("""
				update companyProperty
				set cpValue = '$propertyValue'
				where companyID = $id and masterIndex = (
					select masterIndex
					from companyPropertyMaster
					where masterName = '$propertyName'
				)
			""".trimIndent()
		)

		private fun deleteStatementForProperty(connection: Connection, propertyName: String) = connection.prepareStatement("""
				delete from companyProperty
				where masterIndex = (
					select masterIndex
					from companyPropertyMaster
					where companyID = $id and masterName = '$propertyName'
				)
			""".trimIndent()
		)
	}
}
