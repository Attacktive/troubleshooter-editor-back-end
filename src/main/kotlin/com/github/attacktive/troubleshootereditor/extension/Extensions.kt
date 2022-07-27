package com.github.attacktive.troubleshootereditor.extension

import com.github.attacktive.troubleshootereditor.model.Identifiable

fun <I, T: Identifiable<I>> MutableSet<T>.findById(id: I): T? {
	return stream()
		.filter { it.getId() == id }
		.findFirst()
		.orElse(null)
}
