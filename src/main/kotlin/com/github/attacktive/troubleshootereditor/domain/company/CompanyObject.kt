package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection

object CompanyObject {
	fun selectCompany(connection: Connection): Company {
		val statement = connection.prepareStatement(
			"""
				select
					c.companyID,
					c.CompanyName,
					c.Vill
				from company c
				limit 1
			""".trimIndent()
		)

		val company: Company
		statement.executeQuery().use {
			if (it.next()) {
				val companyId = it.getInt("companyID")
				val companyName = it.getString("CompanyName")
				val vill = it.getLong("Vill")

				company = Company(companyId, companyName, vill)
			} else {
				throw IllegalStateException("The save file doesn't seem to have a company data at all!")
			}
		}

		val propertiesStatement = connection.prepareStatement("""
			select
				cpm.masterName,
				cp.cpValue
			from company c
				left join companyProperty cp on c.companyID = cp.companyID
				left join companyPropertyMaster cpm on cp.masterIndex = cpm.masterIndex
			where c.companyID = ?
		""".trimIndent()
		)

		propertiesStatement.setInt(1, company.id)

		propertiesStatement.executeQuery().use {
			while (it.next()) {
				val key = it.getString("masterName")
				val value = it.getString("cpValue")

				company._properties.add(key to value)
			}
		}

		return company
	}
}
