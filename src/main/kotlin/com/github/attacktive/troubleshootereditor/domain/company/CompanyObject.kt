package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import java.sql.PreparedStatement

object CompanyObject {
	fun selectCompany(connection: Connection): Company {
		val statement = connection.prepareStatement(
			"""
				select
					c.companyID,
					c.CompanyName,
					c.Vill,
					iif(cpm.masterIndex is null, '{}', json_group_object(cpm.masterName, cp.cpValue)) as properties
				from company c
					left join companyProperty cp on c.companyID = cp.companyID
					left join companyPropertyMaster cpm on cp.masterIndex = cpm.masterIndex
				limit 1
			""".trimIndent()
		)

		return getCompanyFromStatement(statement)
	}

	fun selectAndDiff(connection: Connection, newCompany: Company): Company.DiffResult {
		val oldCompany = selectCompany(connection)
		return oldCompany.diff(newCompany)
	}

	private fun getCompanyFromStatement(statement: PreparedStatement): Company {
		statement.executeQuery().use {
			return Company.fromResultSet(it)
		}
	}
}
