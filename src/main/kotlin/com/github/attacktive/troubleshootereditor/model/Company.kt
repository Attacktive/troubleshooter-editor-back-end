package com.github.attacktive.troubleshootereditor.model

data class Company(val companyId: Long, val companyName: String, val vill: Long) {
	val properties: MutableMap<String, String> = mutableMapOf()
}
