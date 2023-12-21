package com.github.attacktive.troubleshootereditor.extension

import com.github.attacktive.troubleshootereditor.domain.Identifiable

fun <I, T: Identifiable<I>> Collection<T>.findById(id: I): T? {
	return asSequence()
		.find { it.getId() == id }
}
