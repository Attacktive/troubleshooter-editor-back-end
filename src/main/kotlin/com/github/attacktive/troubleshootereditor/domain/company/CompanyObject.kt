package com.github.attacktive.troubleshootereditor.domain.company

import java.sql.Connection
import com.github.attacktive.troubleshootereditor.domain.company.table.Companies
import com.github.attacktive.troubleshootereditor.domain.company.table.CompanyProperties
import com.github.attacktive.troubleshootereditor.domain.company.table.CompanyPropertyMaster
import com.github.attacktive.troubleshootereditor.domain.common.Property
import com.github.attacktive.troubleshootereditor.extension.toProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object CompanyObject {
	fun selectCompany(connection: Connection): Company {
		Database.connect(getNewConnection = { connection })

		return transaction {
			addLogger()

			val company = Companies
				.select(Companies.id, Companies.name, Companies.vill)
				.orderBy(Companies.id, SortOrder.DESC)
				.limit(1)
				.first()

			val properties = (CompanyPropertyMaster innerJoin CompanyProperties)
				.select(CompanyPropertyMaster.masterName, CompanyProperties.cpValue)
				.where { CompanyPropertyMaster.masterIndex eq CompanyProperties.masterIndex }
				.map { it[CompanyPropertyMaster.masterName] to it[CompanyProperties.cpValue] }
				.map { Property(it) }
				.toMutableList()
				.toProperties()

			Company(company[Companies.id], company[Companies.name], company[Companies.vill], properties)
		}
	}

	fun selectAndDiff(connection: Connection, newCompany: Company): Company.DiffResult {
		val oldCompany = selectCompany(connection)
		return oldCompany.diff(newCompany)
	}
}
