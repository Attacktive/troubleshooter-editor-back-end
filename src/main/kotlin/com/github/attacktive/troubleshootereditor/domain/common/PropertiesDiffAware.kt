package com.github.attacktive.troubleshootereditor.domain.common

import java.sql.Connection
import java.sql.PreparedStatement

interface PropertiesDiffAware {
	val properties: Properties

	fun generateStatements(connection: Connection): List<PreparedStatement>

	fun getStatementsForProperties(connection: Connection): Sequence<PreparedStatement> = properties.asSequence().mapNotNull { property ->
		when (property.diffType) {
			DiffType.NONE -> null
			DiffType.ADDED -> insertStatementForProperty(connection, property.key, property.value)
			DiffType.MODIFIED -> updateStatementForProperty(connection, property.key, property.value)
			DiffType.REMOVED -> deleteStatementForProperty(connection, property.key)
		}
	}

	fun insertStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement

	fun updateStatementForProperty(connection: Connection, propertyName: String, propertyValue: String): PreparedStatement

	fun deleteStatementForProperty(connection: Connection, propertyName: String): PreparedStatement
}
