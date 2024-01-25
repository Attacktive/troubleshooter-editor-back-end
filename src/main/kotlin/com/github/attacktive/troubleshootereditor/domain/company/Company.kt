package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesAware
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware
import com.github.attacktive.troubleshootereditor.extension.deserializeAsStringToStringMap

data class Company(val id: Int, val name: String, val vill: Long): PropertiesAware {
	constructor(id: Int, name: String, vill: Long, propertiesJson: String): this(id, name, vill) {
		addProperties(propertiesJson.deserializeAsStringToStringMap())
	}

	override val properties: Properties = Properties()

	companion object {
		fun fromResultSet(resultSet: ResultSet): Company {
			if (resultSet.next()) {
				val companyId = resultSet.getInt("companyID")
				val companyName = resultSet.getString("CompanyName")
				val vill = resultSet.getLong("Vill")
				val properties = resultSet.getString("properties")

				return Company(companyId, companyName, vill, properties)
			} else {
				throw IllegalStateException("The save file doesn't seem to have a company data at all!")
			}
		}
	}

	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(val id: Int, val name: String?, val vill: Long?, override val properties: Properties): PropertiesDiffAware {
		override fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: List<PreparedStatement> = mutableListOf()

			if (name != null) {
				statements.addLast(updateStatementForName(connection))
			}

			if (vill != null) {
				statements.addLast(updateStatementForVill(connection))
			}

			getStatementsForProperties(connection).forEach { statements.addLast(it) }

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

		override fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement("""
				insert into companyProperty (companyId, masterIndex, cpValue)
				select
					$id,
					cpm.masterIndex,
					'$propertyValue'
				from companyPropertyMaster cpm
				where masterName = '$propertyName'
			""".trimIndent()
		)

		override fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement("""
				update companyProperty
				set cpValue = '$propertyValue'
				where companyID = $id and masterIndex = (
					select masterIndex
					from companyPropertyMaster
					where masterName = '$propertyName'
				)
			""".trimIndent()
		)

		override fun deleteStatementForProperty(connection: Connection, propertyName: String): PreparedStatement = connection.prepareStatement("""
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
