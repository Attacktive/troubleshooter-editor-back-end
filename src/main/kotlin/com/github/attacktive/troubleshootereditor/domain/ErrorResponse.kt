package com.github.attacktive.troubleshootereditor.domain

data class ErrorResponse(val message: String?, val throwable: Exception? = null) {
	constructor(throwable: Exception): this(throwable.message, throwable)
}
