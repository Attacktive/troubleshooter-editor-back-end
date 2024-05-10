package com.github.attacktive.troubleshootereditor.domain.common

import java.util.function.Predicate

data class Properties(private val list: MutableList<Property> = mutableListOf()) {
	constructor(map: Map<String, String>): this(map.map { Property(it.toPair()) }.toMutableList())

	fun containsKey(key: String) = keys().contains(key)
	fun containsKeyThat(predicate: Predicate<String>) = keys().any { predicate.test(it) }

	fun add(pair: Pair<String, String>) = list.addLast(Property(pair))
	fun forEach(action: (Property) -> Unit) = list.forEach(action)
	fun toMap() = list.associate { it.key to it.value }

	fun diff(those: Properties): Properties {
		val thoseKeys = those.keys().toMutableList()

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

		return Properties((withThese + withThose).toMutableList())
	}

	private fun keys() = list.map { it.key }.toSet()

	private fun findByKey(key: String) = list.find { it.key == key }
}
