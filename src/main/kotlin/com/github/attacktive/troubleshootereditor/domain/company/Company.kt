package com.github.attacktive.troubleshootereditor.domain.company

import com.fasterxml.jackson.annotation.JsonGetter
import com.github.attacktive.troubleshootereditor.domain.common.IDiffResult
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.company.table.CompanyProperties
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

data class Company(val id: Int, val name: String, val vill: Long, val properties: Properties) {
	constructor(id: Int, name: String, vill: Long, properties: Map<String, String>): this(id, name, vill, Properties(properties))

	@JsonGetter(value = "properties")
	fun properties() = properties.toMap().toSortedMap()

	fun diff(that: Company): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val vill = that.vill.takeUnless { vill == that.vill }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, vill, properties)
	}

	data class DiffResult(override val id: Int, val name: String?, val vill: Long?, val properties: Properties): IDiffResult<Int> {
		override fun insert(propertyIndex: Int, propertyValue: String) {
			CompanyProperties.insert {
				it[companyId] = id
				it[masterIndex] = propertyIndex
				it[cpValue] = propertyValue
			}
		}

		override fun update(propertyIndex: Int, propertyValue: String) {
			CompanyProperties.update({ (CompanyProperties.companyId eq id) and (CompanyProperties.masterIndex eq propertyIndex) }) {
				it[cpValue] = propertyValue
			}
		}

		override fun delete(propertyIndex: Int) {
			CompanyProperties.deleteWhere {
				(companyId eq id) and (masterIndex eq propertyIndex)
			}
		}
	}
}
