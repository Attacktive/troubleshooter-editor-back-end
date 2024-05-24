package com.github.attacktive.troubleshootereditor.domain.company

import com.github.attacktive.troubleshootereditor.domain.common.Property
import com.github.attacktive.troubleshootereditor.domain.company.table.Companies
import com.github.attacktive.troubleshootereditor.domain.company.table.CompanyProperties
import com.github.attacktive.troubleshootereditor.domain.company.table.CompanyPropertyMaster
import com.github.attacktive.troubleshootereditor.extension.toProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object CompanyObject {
	fun selectCompany(url: String): Company {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

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

	fun saveChanges(url: String, newCompany: Company) {
		val oldCompany = selectCompany(url)
		val diffResult = oldCompany.diff(newCompany)

		val companyMasterLookup = getCompanyMasterLookup()

		transaction {
			addLogger(StdOutSqlLogger)

			if (diffResult.name != null) {
				Companies.update({ Companies.id eq diffResult.id }) {
					it[name] = diffResult.name
				}
			}

			if (diffResult.vill != null) {
				Companies.update({ Companies.id eq diffResult.id }) {
					it[vill] = diffResult.vill
				}
			}

			diffResult.properties.applyPropertyChanges(diffResult, companyMasterLookup)
		}
	}

	private fun getCompanyMasterLookup() = transaction {
		addLogger(StdOutSqlLogger)

		CompanyPropertyMaster.select(CompanyPropertyMaster.masterIndex, CompanyPropertyMaster.masterName)
			.associate { it[CompanyPropertyMaster.masterName] to it[CompanyPropertyMaster.masterIndex] }
	}
}
