package com.github.attacktive.troubleshootereditor.model

data class Properties(private val properties: List<Property> = mutableListOf()) {
	private val mutableKeys = properties.map { it.key }.toMutableList()

	fun isNotEmpty() = properties.isNotEmpty()
	fun add(pair: Pair<String, String>) = properties.addLast(Property(pair))
	fun <T> map(transform: (Property) -> T) = properties.asSequence().map(transform)

	private fun findByKey(key: String) = properties.find { it.key == key }

	fun diff(those: Properties): Properties {
		val thoseKeys = those.mutableKeys

		val withThese = properties.map { `this` ->
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
