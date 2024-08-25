package com.github.attacktive.troubleshootereditor.ingamedata.common

import java.util.function.Predicate
import com.github.attacktive.troubleshootereditor.common.extension.logger

open class Properties(private val list: List<Property> = mutableListOf()) {
	constructor(map: Map<String, String>): this(map.map { Property(it.toPair()) }.toMutableList())

	private val logger by logger()

	fun containsKey(key: String) = keys().contains(key)
	fun containsKeyThat(predicate: Predicate<String>) = keys().any { predicate.test(it) }

	fun toMap() = list.associate { it.key to it.value }

	fun diff(those: Properties): Properties {
		val thoseKeys = those.keys().toMutableList()

		val withThese = list.map { `this` ->
			val key = `this`.key
			val thisValue = `this`.value

			thoseKeys.remove(key)

			val that = those.findByKey(key)
			val typeChanged = if (that == null) {
				`this`.withDiffType(DiffType.REMOVED)
			} else {
				val thatValue = that.value
				if (thisValue == thatValue) {
					that
				} else {
					that.withDiffType(DiffType.MODIFIED)
				}
			}

			typeChanged
		}

		val withThose = thoseKeys.map { thatKey ->
			val that = those.findByKey(thatKey)!!
			that.withDiffType(DiffType.ADDED)
		}

		return Properties((withThese + withThose).toMutableList())
	}

	fun <T> applyPropertyChanges(diffResult: IDiffResult<T>, propertyMasterLookup: Map<String, Int>) {
		for (property in list) {
			val propertyIndex = propertyMasterLookup[property.key]
			if (propertyIndex == null) {
				logger.warn("Failed to find item property master index for \"${property.key}\"; ignoring. 😞")
			} else {
				when (property.diffType) {
					DiffType.NONE -> {}
					DiffType.ADDED -> diffResult.insert(propertyIndex, property.value)
					DiffType.MODIFIED -> diffResult.update(propertyIndex, property.value)
					DiffType.REMOVED -> diffResult.delete(propertyIndex)
				}
			}
		}
	}

	private fun keys() = list.map { it.key }.toSet()

	private fun findByKey(key: String) = list.find { it.key == key }
}
