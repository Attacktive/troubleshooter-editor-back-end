package com.github.attacktive.troubleshootereditor.domain.roster

import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Properties

data class Roster(val id: Long, val name: String, val `class`: String, val level: Long, val exp: Long, val properties: Properties): Diffable<Roster, Long, Roster.DiffResult> {
	override fun getId() = id

	override fun diff(that: Roster): DiffResult {
		val name = that.name.takeUnless { name == that.name }
		val `class` = that.`class`.takeUnless { `class` == that.`class` }
		val level = that.level.takeUnless { level == that.level }
		val exp = that.exp.takeUnless { exp == that.exp }
		val properties = properties.diff(that.properties)

		return DiffResult(id, name, `class`, level, exp, properties)
	}

	data class DiffResult(val id: Long, val name: String?, val `class`: String?, val level: Long?, val exp: Long?, val properties: Properties)
}
