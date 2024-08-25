package com.github.attacktive.troubleshootereditor.ingamedata.common

interface Diffable<T, I, D>: Identifiable<I> {
	fun diff(that: T): D
}
