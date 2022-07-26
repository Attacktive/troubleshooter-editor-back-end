package com.github.attacktive.troubleshootereditor.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.attacktive.troubleshootereditor.model.compnay.Company

data class SaveData(val company: Company) {
	override fun toString(): String {
		val objectMapper = ObjectMapper()
		return objectMapper.writeValueAsString(this)
	}
}
