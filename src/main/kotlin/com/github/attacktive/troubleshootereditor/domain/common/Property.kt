package com.github.attacktive.troubleshootereditor.domain.common

data class Property(private val pair: Pair<String, String>, val diffType: DiffType = DiffType.NONE) {
	val key = pair.first
	val value = pair.second

	fun withDiffType(diffType: DiffType) = Property(pair, diffType)
}
