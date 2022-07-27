package com.github.attacktive.troubleshootereditor.model

data class Company(val id: Long, val name: String, val vill: Long) {
	val properties: MutableMap<String, String> = mutableMapOf()
}
