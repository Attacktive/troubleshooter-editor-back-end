package com.github.attacktive.troubleshootereditor.domain.common

interface Diffable<T, I, D>: Identifiable<I> {
	fun diff(that: T): D
}
