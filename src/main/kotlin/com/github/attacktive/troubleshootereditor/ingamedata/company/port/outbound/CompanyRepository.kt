package com.github.attacktive.troubleshootereditor.ingamedata.company.port.outbound

import com.github.attacktive.troubleshootereditor.ingamedata.company.domain.Company

interface CompanyRepository {
	fun selectCompany(url: String): Company
	fun saveChanges(url: String, newCompany: Company)
}
