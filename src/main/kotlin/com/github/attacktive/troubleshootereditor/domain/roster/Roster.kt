package com.github.attacktive.troubleshootereditor.domain.roster

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesAware
import com.github.attacktive.troubleshootereditor.domain.common.PropertiesDiffAware
import com.github.attacktive.troubleshootereditor.extension.deserializeAsStringToStringMap

data class Roster(val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long): Diffable<Roster, Long, Roster.DiffResult>, PropertiesAware {
	constructor(id: Long, name: String, `class`: String, level: Long, exp: Long, propertiesJson: String): this(id, name, `class`, level, exp) {
		addProperties(propertiesJson.deserializeAsStringToStringMap())
	}

	override val properties: Properties = Properties()

	override fun getId() = id

	companion object {
		fun fromResultSet(resultSet: ResultSet): List<Roster> {
			val rosters: List<Roster> = mutableListOf()

			while (resultSet.next()) {
				val id = resultSet.getLong("rosterID")
				val name = resultSet.getString("rosterName")
				val `class` = resultSet.getString("rosterClass")
				val level = resultSet.getLong("rosterLv")
				val exp = resultSet.getLong("rosterExp")
				val properties = resultSet.getString("properties")

				rosters.addLast(Roster(id, name, `class`, level, exp, properties))
			}

			return rosters
		}
	}

	override fun diff(that: Roster): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val `class` = that.`class`.takeUnless { `class` == that.`class` }
		val level = that.level.takeUnless { level == that.level }
		val exp = that.exp.takeUnless { exp == that.exp }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, `class`, level, exp, properties)
	}

	data class DiffResult(val id: Long, val name: String?, val `class`: String?, val level: Long?, val exp: Long?, override val properties: Properties): PropertiesDiffAware {
		override fun generateStatements(connection: Connection): List<PreparedStatement> {
			val statements: List<PreparedStatement> = mutableListOf()

			if (name != null) {
				statements.addLast(updateStatementForName(connection))
			}

			if (`class` != null) {
				statements.addLast(updateStatementForClass(connection))
			}

			if (level != null) {
				statements.addLast(updateStatementForLevel(connection))
			}

			if (exp != null) {
				statements.addLast(updateStatementForExp(connection))
			}

			getStatementsForProperties(connection).forEach { statements.addLast(it) }

			return statements
		}

		override fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement(
			"""
				insert into rosterProperty (rosterID, masterIndex, rpValue)
				select
					$id,
					ipm.masterIndex,
					'$propertyValue'
				from rosterPropertyMaster ipm
				where masterName = '$propertyName'
			""".trimIndent()
		)

		override fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement = connection.prepareStatement(
			"""
				update rosterProperty
				set cpValue = '$propertyValue'
				where rosterID = $id and masterIndex = (
					select masterIndex
					from rosterPropertyMaster
					where masterName = '$propertyName'
				)
			""".trimIndent()
		)

		override fun deleteStatementForProperty(connection: Connection, propertyName: String): PreparedStatement = connection.prepareStatement(
			"""
				delete from main.rosterProperty
				where masterIndex = (
					select masterIndex
					from main.rosterPropertyMaster
					where rosterID = $id and masterName = '$propertyName'
				)
			""".trimIndent()
		)

		private fun updateStatementForName(connection: Connection) = connection.prepareStatement("""
				update roster
				set rosterName = '$name'
				where rosterID = $id
			""".trimIndent()
		)

		private fun updateStatementForClass(connection: Connection) = connection.prepareStatement("""
				update roster
				set rosterClass = '$`class`'
				where rosterID = $id
			""".trimIndent()
		)

		private fun updateStatementForLevel(connection: Connection) = connection.prepareStatement("""
				update roster
				set rosterLv = '$level'
				where rosterID = $id
			""".trimIndent()
		)

		private fun updateStatementForExp(connection: Connection) = connection.prepareStatement("""
				update roster
				set rosterExp = '$exp'
				where rosterID = $id
			""".trimIndent()
		)
	}
}
