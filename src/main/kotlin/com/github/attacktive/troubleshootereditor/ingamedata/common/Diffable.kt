package com.github.attacktive.troubleshootereditor.ingamedata.common

interface Diffable<T, D> {
	fun diff(that: T): D
}
