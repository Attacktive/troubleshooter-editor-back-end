package com.github.attacktive.troubleshootereditor.ingamedata.roster.port.outbound

import com.github.attacktive.troubleshootereditor.ingamedata.roster.domain.Roster

interface RosterRepository {
	fun selectRosters(url: String): List<Roster>

	fun saveChanges(url: String, newRosters: Collection<Roster>)
}
