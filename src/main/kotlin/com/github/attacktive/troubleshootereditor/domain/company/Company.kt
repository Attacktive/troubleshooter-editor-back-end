package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.domain.DiffType
import com.github.attacktive.troubleshootereditor.domain.Properties

data class Company(val id: Int, val name: String, val vill: Long, private val _properties: Properties = Properties()) {
	@Suppress("unused")
	val properties get() = _properties.toMap()

	fun addProperty(property: Pair<String, String>) {
		_properties.add(property)
	}

	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = _properties.diff(that._properties)

		return DiffResult(id, name, vill, properties)
	}

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
