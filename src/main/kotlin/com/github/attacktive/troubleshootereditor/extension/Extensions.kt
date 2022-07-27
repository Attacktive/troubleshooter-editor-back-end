package com.github.attacktive.troubleshootereditor.extension

import com.github.attacktive.troubleshootereditor.model.Quest

fun MutableSet<Quest>.findByIndex(index: Long): Quest? {
	return stream()
		.filter { it.index == index }
		.findFirst()
		.orElse(null)
}
