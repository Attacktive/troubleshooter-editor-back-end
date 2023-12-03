package com.github.attacktive.troubleshootereditor.common.extension

import com.github.attacktive.troubleshootereditor.module.Identifiable

fun <I, T: Identifiable<I>> MutableSet<T>.findById(id: I): T? {
	return stream()
		.filter { it.getId() == id }
		.findFirst()
		.orElse(null)
}
