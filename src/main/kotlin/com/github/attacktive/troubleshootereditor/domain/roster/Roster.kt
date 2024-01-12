package com.github.attacktive.troubleshootereditor.domain.roster

import com.github.attacktive.troubleshootereditor.domain.common.Identifiable

class Roster(private val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long): Identifiable<Long> {
	val properties: MutableMap<String, String> = mutableMapOf()

	override fun getId(): Long {
		return id
	}
}
