package com.github.attacktive.troubleshootereditor.domain.roster

import java.sql.Connection
import com.github.attacktive.troubleshootereditor.extension.getDiffResults

object RosterObject {
	fun selectRosters(connection: Connection): List<Roster> {
		val statement = connection.prepareStatement(
			"""
				select
					r.rosterID,
					r.rosterName,
					r.rosterClass,
					r.rosterLv,
					r.rosterExp,
					iif(rpm.masterIndex is null, '{}', json_group_object(rpm.masterName, rp.rpValue)) as properties
				from roster r
					left join rosterProperty rp on r.rosterID = rp.rosterID
					left join rosterPropertyMaster rpm on rp.masterIndex = rpm.masterIndex
				group by r.rosterID,r.rosterName,r.rosterClass,r.rosterLv,r.rosterExp
				order by r.rosterID
			""".trimIndent()
		)

		val rosters = mutableListOf<Roster>()
		statement.executeQuery().use {
			while (it.next()) {
				val roster = Roster.fromResultSet(it)
				rosters.add(roster)
			}
		}

		return rosters
	}

	fun selectAndDiff(connection: Connection, newRosters: Collection<Roster>): List<Roster.DiffResult> {
		val oldRosters = selectRosters(connection)
		return oldRosters.getDiffResults(newRosters)
	}
}
