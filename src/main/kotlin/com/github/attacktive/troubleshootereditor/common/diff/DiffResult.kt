package com.github.attacktive.troubleshootereditor.common.diff

interface DiffResult {
	val properties: PropertiesDiffResult?
	val hasChanges: Boolean
}
