package com.github.attacktive.troubleshootereditor.ingamedata.common

interface IDiffResult<T>: Identifiable<T> {
	fun insert(propertyIndex: Int, propertyValue: String)
	fun update(propertyIndex: Int, propertyValue: String)
	fun delete(propertyIndex: Int)
}
