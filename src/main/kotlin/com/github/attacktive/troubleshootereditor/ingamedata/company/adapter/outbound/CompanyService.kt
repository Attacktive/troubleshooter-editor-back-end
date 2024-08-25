package com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound

import com.github.attacktive.troubleshootereditor.common.extension.toProperties
import com.github.attacktive.troubleshootereditor.ingamedata.common.Property
import com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound.table.Companies
import com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound.table.CompanyProperties
import com.github.attacktive.troubleshootereditor.ingamedata.company.adapter.outbound.table.CompanyPropertyMaster
import com.github.attacktive.troubleshootereditor.ingamedata.company.domain.Company
import com.github.attacktive.troubleshootereditor.ingamedata.company.port.outbound.CompanyRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Service

@Service
class CompanyService: CompanyRepository {
	override fun selectCompany(url: String): Company {
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

	override fun saveChanges(url: String, newCompany: Company) {
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
