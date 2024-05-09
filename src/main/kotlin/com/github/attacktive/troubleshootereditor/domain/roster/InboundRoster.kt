package com.github.attacktive.troubleshootereditor.domain.roster

import com.github.attacktive.troubleshootereditor.domain.common.Properties

data class InboundRoster(val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long, val properties: Properties) {
	fun toRoster() = Roster(id, name, `class`, level, exp, properties)
}
