package com.github.attacktive.troubleshootereditor.domain.common

data class Properties(private val list: List<Property> = mutableListOf()) {
	fun add(pair: Pair<String, String>) = list.addLast(Property(pair))
	fun asSequence() = list.asSequence()
	fun toMap() = list.associate { it.key to it.value }

	private fun findByKey(key: String) = list.find { it.key == key }

	fun diff(those: Properties): Properties {
		val thoseKeys = those.list.map { it.key }.toMutableList()

		val withThese = list.map { `this` ->
			val key = `this`.key
			val thisValue = `this`.value

			thoseKeys.remove(key)

			val that = those.findByKey(key)
			if (that == null) {
				`this`.withDiffType(DiffType.REMOVED)
			} else {
				val thatValue = that.value
				if (thisValue == thatValue) {
					that
				} else {
					that.withDiffType(DiffType.MODIFIED)
				}
			}
		}

		val withThose = thoseKeys.map { thatKey ->
			val that = those.findByKey(thatKey)!!
			that.withDiffType(DiffType.ADDED)
		}

		return Properties(withThese + withThose)
	}
}
