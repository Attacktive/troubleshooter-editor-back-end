package com.github.attacktive.troubleshootereditor.common.diff

object MapDiffUtils {
	fun diff(source: Map<String, String>, desired: Map<String, String>): PropertiesDiffResult {
		val inserts = mutableListOf<Pair<String, String>>()
		val updates = mutableListOf<Pair<String, String>>()
		val deletes = mutableListOf<String>()

		for (key in source.keys) {
			val sourceValue = source[key]
			if (desired.containsKey(key)) {
				val desiredValue = desired[key]

				if (sourceValue != desiredValue && desiredValue != null) {
					updates.add(key to desiredValue)
				}
			} else {
				deletes.add(key)
			}
		}

		for (key in desired.keys) {
			val desiredValue = desired[key]
			if (desiredValue != null && !source.containsKey(key)) {
				inserts.add(key to desiredValue)
			}
		}

		return PropertiesDiffResult(inserts, updates, deletes)
	}
}