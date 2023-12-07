package com.github.attacktive.troubleshootereditor.extension

import com.github.attacktive.troubleshootereditor.model.Identifiable

fun <I, T: Identifiable<I>> Collection<T>.findById(id: I): T? {
	return asSequence()
		.find { it.getId() == id }
}
