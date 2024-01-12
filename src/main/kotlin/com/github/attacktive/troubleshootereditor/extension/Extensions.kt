package com.github.attacktive.troubleshootereditor.extension

import java.io.File
import kotlin.reflect.full.companionObject
import com.github.attacktive.troubleshootereditor.domain.common.Identifiable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun File.getJdbcUrl() = "jdbc:sqlite:${absolutePath}"

fun <I, T: Identifiable<I>> Collection<T>.findById(id: I): T? {
	return asSequence()
		.find { it.getId() == id }
}

fun <T: Any> T.logger(): Lazy<Logger> {
	val className = unwrapCompanionClass(javaClass).name

	return lazy { LoggerFactory.getLogger(className) }
}

/**
 * Unwraps companion class to enclosing class given a Java Class
 */
fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
	return ofClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == ofClass } ?: ofClass
}
