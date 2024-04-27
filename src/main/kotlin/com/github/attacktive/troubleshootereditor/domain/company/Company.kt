package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesAware
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware

data class Company(val id: Int, val name: String, val vill: Long, override val properties: Properties): PropertiesAware {
	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(val id: Int, val name: String?, val vill: Long?, override val properties: Properties): PropertiesDiffAware {
		override fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: MutableList<PreparedStatement> = mutableListOf()

			if (name != null) {
				statements.add(updateStatementForName(connection))
			}

			if (vill != null) {
				statements.add(updateStatementForVill(connection))
			}

			getStatementsForProperties(connection).forEach { statements.add(it) }

			return statements
		}

		private fun updateStatementForName(connection: Connection) = connection.prepareStatement(
			"""
				update company
				set CompanyName = '$name'
				where companyID = $id
			""".trimIndent()
		)

		private fun updateStatementForVill(connection: Connection) = connection.prepareStatement(
			"""
				update company
				set Vill = $vill
				where companyID = $id
			""".trimIndent()
		)

		override fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement(
			"""
				insert into companyProperty (companyId, masterIndex, cpValue)
				select
					$id,
					cpm.masterIndex,
					'$propertyValue'
				from companyPropertyMaster cpm
				where masterName = '$propertyName'
			""".trimIndent()
		)

		override fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement(
			"""
				update companyProperty
				set cpValue = '$propertyValue'
				where companyID = $id and masterIndex = (
					select masterIndex
					from companyPropertyMaster
					where masterName = '$propertyName'
				)
			""".trimIndent()
		)

		override fun deleteStatementForProperty(connection: Connection, propertyName: String): PreparedStatement = connection.prepareStatement(
			"""
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
