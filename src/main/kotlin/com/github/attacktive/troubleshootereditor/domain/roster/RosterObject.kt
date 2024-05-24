package com.github.attacktive.troubleshootereditor.domain.roster

import com.github.attacktive.troubleshootereditor.domain.common.Property
import com.github.attacktive.troubleshootereditor.domain.roster.table.RosterProperties
import com.github.attacktive.troubleshootereditor.domain.roster.table.RosterPropertyMaster
import com.github.attacktive.troubleshootereditor.domain.roster.table.Rosters
import com.github.attacktive.troubleshootereditor.extension.getDiffResults
import com.github.attacktive.troubleshootereditor.extension.logger
import com.github.attacktive.troubleshootereditor.extension.toProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object RosterObject {
	private val logger by logger()

	fun selectRosters(url: String): List<Roster> {
		Database.connect(url)

		return transaction {
			addLogger(StdOutSqlLogger)

			(Rosters leftJoin RosterProperties leftJoin RosterPropertyMaster)
				.select(Rosters.id, Rosters.name, Rosters.`class`, Rosters.level, Rosters.exp, RosterPropertyMaster.name, RosterProperties.value)
				.where {
					(Rosters.id eq RosterProperties.rosterId) and (RosterProperties.masterIndex eq RosterPropertyMaster.index)
				}
				.orderBy(Rosters.id)
				.groupBy { it[Rosters.id] }
				.values
				.map { resultRows ->
					val properties = resultRows.map { resultRow -> resultRow[RosterPropertyMaster.name] to resultRow[RosterProperties.value] }
						.map { Property(it) }
						.toMutableList()
						.toProperties()

					val resultRow = resultRows[0]
					Roster(resultRow[Rosters.id], resultRow[Rosters.name], resultRow[Rosters.`class`], resultRow[Rosters.level], resultRow[Rosters.exp], properties)
				}
		}
	}

	fun saveChanges(url: String, newRosters: Collection<Roster>) {
		Database.connect(url)

		val oldRosters = selectRosters(url)
		val diffResult = oldRosters.getDiffResults(newRosters)

		val rosterPropertyMasterLookup = getRosterPropertyMasterLookup()

		transaction {
			addLogger(StdOutSqlLogger)

			diffResult.forEach { rosterDiff ->
				if (rosterDiff.name != null) {
					Rosters.update({ Rosters.id eq rosterDiff.id }) {
						it[name] = rosterDiff.name
					}
				}

				if (rosterDiff.`class` != null) {
					Rosters.update({ Rosters.id eq rosterDiff.id }) {
						it[`class`] = rosterDiff.`class`
					}
				}

				if (rosterDiff.level != null) {
					Rosters.update({ Rosters.id eq rosterDiff.id }) {
						it[level] = rosterDiff.level
					}
				}

				if (rosterDiff.exp != null) {
					Rosters.update({ Rosters.id eq rosterDiff.id }) {
						it[exp] = rosterDiff.exp
					}
				}

				rosterDiff.properties.applyPropertyChanges(rosterDiff, rosterPropertyMasterLookup)
			}
		}
	}

	private fun getRosterPropertyMasterLookup() = transaction {
		addLogger(StdOutSqlLogger)

		RosterPropertyMaster.select(RosterPropertyMaster.index, RosterPropertyMaster.name)
			.associate { it[RosterPropertyMaster.name] to it[RosterPropertyMaster.index] }
	}
}
