package com.github.attacktive.troubleshootereditor.ingamedata.roster.domain

import com.github.attacktive.troubleshootereditor.ingamedata.common.Diffable
import com.github.attacktive.troubleshootereditor.ingamedata.common.IDiffResult
import com.github.attacktive.troubleshootereditor.ingamedata.common.Properties
import com.github.attacktive.troubleshootereditor.ingamedata.roster.adapter.outbound.table.RosterProperties
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

data class Roster(override val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long, val properties: Properties): Diffable<Roster, Long, Roster.DiffResult> {
	override fun diff(that: Roster): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val `class` = that.`class`.takeUnless { `class` == that.`class` }
		val level = that.level.takeUnless { level == that.level }
		val exp = that.exp.takeUnless { exp == that.exp }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, `class`, level, exp, properties)
	}

	data class DiffResult(override val id: Long, val name: String?, val `class`: String?, val level: Long?, val exp: Long?, val properties: Properties): IDiffResult<Long> {
		override fun insert(propertyIndex: Int, propertyValue: String) {
			RosterProperties.insert {
				it[rosterId] = id
				it[masterIndex] = propertyIndex
				it[value] = propertyValue
			}
		}

		override fun update(propertyIndex: Int, propertyValue: String) {
			RosterProperties.update({ (RosterProperties.rosterId eq id) and (RosterProperties.masterIndex eq propertyIndex) }) {
				it[value] = propertyValue
			}
		}

		override fun delete(propertyIndex: Int) {
			RosterProperties.deleteWhere {
				(rosterId eq id) and (masterIndex eq propertyIndex)
			}
		}
	}
}
