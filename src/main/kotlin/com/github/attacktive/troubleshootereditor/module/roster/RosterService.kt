package com.github.attacktive.troubleshootereditor.module.roster

import java.sql.Connection
import com.github.attacktive.troubleshootereditor.common.extension.findById

object RosterService {
	fun selectRosters(connection: Connection): List<Roster> {
		val statement = connection.prepareStatement(
			"""
				select
					r.rosterID,
					r.rosterName,
					r.rosterClass,
					r.rosterLv,
					r.rosterExp,
					rpm.masterName,
					rp.rpValue
				from roster r
					left join rosterProperty rp on r.rosterID = rp.rosterID
					left join rosterPropertyMaster rpm on rp.masterIndex = rpm.masterIndex
			""".trimIndent()
		)

		val rosters = mutableSetOf<Roster>()
		statement.executeQuery().use {
			while (it.next()) {
				val id = it.getLong("rosterID")
				var roster = rosters.findById(id)
				if (roster == null) {
					val name = it.getString("rosterName")
					val `class` = it.getString("rosterClass")
					val level = it.getLong("rosterLv")
					val exp = it.getLong("rosterExp")
					roster = Roster(id, name, `class`, level, exp)
					rosters.add(roster)
				}

				val propertyName = it.getString("masterName")
				val propertyValue = it.getString("rpValue")

				roster.properties[propertyName] = propertyValue
			}
		}

		return rosters.toList()
	}
}
