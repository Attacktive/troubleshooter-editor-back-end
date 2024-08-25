package com.github.attacktive.troubleshootereditor.ingamedata.common

data class ErrorResponse(val message: String?, val throwable: Exception? = null) {
	constructor(throwable: Exception): this(throwable.message, throwable)
}
