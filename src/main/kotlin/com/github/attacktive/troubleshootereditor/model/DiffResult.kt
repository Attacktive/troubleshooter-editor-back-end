package com.github.attacktive.troubleshootereditor.model

import java.sql.Connection
import java.sql.PreparedStatement

interface DiffResult {
	val hasChanges: Boolean

	fun getStatements(connection: Connection): MutableList<PreparedStatement>
}
