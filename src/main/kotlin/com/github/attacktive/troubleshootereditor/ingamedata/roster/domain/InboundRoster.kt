package com.github.attacktive.troubleshootereditor.ingamedata.roster.domain

import com.github.attacktive.troubleshootereditor.ingamedata.common.Properties

data class InboundRoster(val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long, val properties: Properties) {
	fun toRoster() = Roster(id, name, `class`, level, exp, properties)
}
