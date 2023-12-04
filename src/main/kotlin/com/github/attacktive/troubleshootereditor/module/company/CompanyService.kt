package com.github.attacktive.troubleshootereditor.module.company

import java.sql.Connection
import java.sql.PreparedStatement
import com.github.attacktive.troubleshootereditor.module.common.MasterTableDetails
import com.github.attacktive.troubleshootereditor.module.common.PropertiesService

object CompanyService {
	fun selectCompany(connection: Connection): Company {
		val statement = connection.prepareStatement(
			"""
				select
					c.companyID,
					c.CompanyName,
					c.Vill,
					cpm.masterName,
					cp.cpValue
				from company c
					left join companyProperty cp on c.companyID = cp.companyID
					left join companyPropertyMaster cpm on cp.masterIndex = cpm.masterIndex
			""".trimIndent()
		)

		var company: Company? = null
		statement.executeQuery().use {
			while (it.next()) {
				val companyId = it.getLong("companyID")
				val companyName = it.getString("CompanyName")
				val vill = it.getLong("Vill")

				if (company == null) {
					company = Company(companyId, companyName, vill)

					val key = it.getString("masterName")
					val value = it.getString("cpValue")
					company!!.properties[key] = value
				}
			}
		}

		return company!!
	}

	fun getStatements(diffResult: Company.DiffResult, connection: Connection): MutableList<PreparedStatement> {
		if (!diffResult.hasChanges) {
			throw IllegalStateException("No statements when no changes.")
		}

		var toUpdateCompany = false

		val setClauseBuilder = mutableListOf<Pair<String, Any>>()
		if (diffResult.name != null) {
			setClauseBuilder.add("CompanyName = ?" to diffResult.name)
			toUpdateCompany = true
		}

		if (diffResult.vill != null) {
			setClauseBuilder.add("Vill = ?" to diffResult.vill)
			toUpdateCompany = true
		}

		var companyStatement: PreparedStatement? = null
		if (toUpdateCompany) {
			val setClause = setClauseBuilder.joinToString(", ") { it.first }

			val companyQuery = """
				update company
				$setClause
				where companyID = ${diffResult.id}
				""".trimIndent()

			companyStatement = connection.prepareStatement(companyQuery)

			setClauseBuilder.forEachIndexed { index, pair -> companyStatement.setObject((index + 1), pair.second) }
		}

		val statements = PropertiesService.getPropertiesStatements(connection, diffResult, MasterTableDetails("companyID", "masterIndex", "cpValue"))

		if (companyStatement != null) {
			statements.add(companyStatement)
		}

		return statements
	}
}
