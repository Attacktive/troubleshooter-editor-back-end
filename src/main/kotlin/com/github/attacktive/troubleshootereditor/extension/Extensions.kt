package com.github.attacktive.troubleshootereditor.extension

import java.io.File
import kotlin.reflect.full.companionObject
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.IDiffResult
import com.github.attacktive.troubleshootereditor.domain.common.Identifiable
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.Property
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Iterable<Property>.toProperties() = Properties(toMutableList())

inline infix fun <reified E: Enum<E>, V> ((E) -> V).findByOrNull(value: V): E? {
	return enumValues<E>().firstOrNull { this(it) == value }
}

fun File.getJdbcUrl() = "jdbc:sqlite:${absolutePath}?rewriteBatchedInserts=true"

fun <I, T: Identifiable<I>> Collection<T>.findById(id: I): T? = asSequence().find { it.id == id }

fun <I, T: Diffable<T, I, D>, D: IDiffResult<I>> Collection<T>.getDiffResults(those: Collection<T>): List<D> {
	return asSequence()
		.mapNotNull { oldItem ->
			val newItem = those.findById(oldItem.id)
			if (newItem == null) {
				// no plan for addition nor deletion yet
				null
			} else {
				oldItem.diff(newItem)
			}
		}
		.toList()
}

fun <T: Any> T.logger(): Lazy<Logger> {
	val className = unwrapCompanionClass(javaClass).name

	return lazy { LoggerFactory.getLogger(className) }
}

/**
 * Unwraps companion class to enclosing class given a Java Class
 */
fun <T: Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
	return ofClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == ofClass } ?: ofClass
}
