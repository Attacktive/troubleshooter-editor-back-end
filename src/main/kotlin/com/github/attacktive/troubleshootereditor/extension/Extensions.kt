package com.github.attacktive.troubleshootereditor.extension

import java.io.File
import java.util.function.Supplier
import kotlin.reflect.full.companionObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.attacktive.troubleshootereditor.domain.common.Diffable
import com.github.attacktive.troubleshootereditor.domain.common.Identifiable
import com.github.attacktive.troubleshootereditor.domain.common.Properties
import com.github.attacktive.troubleshootereditor.domain.common.Property
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Iterable<Property>.toProperties() = Properties(toMutableList())

inline infix fun <reified E : Enum<E>, V> ((E) -> V).findBy(value: V): E {
	return findBy(value) { IllegalArgumentException("No enum constant ${javaClass.canonicalName}.$value.") }
}

inline fun <reified E : Enum<E>, V> ((E) -> V).findBy(value: V, throwableSupplier: Supplier<Throwable>): E {
	return enumValues<E>().firstOrNull { this(it) == value } ?: throw throwableSupplier.get()
}

fun File.getJdbcUrl() = "jdbc:sqlite:${absolutePath}"

fun String.deserializeAsStringToStringMap(): Map<String, String> {
	val objectMapper = jacksonObjectMapper()
	return objectMapper.readValue(this)
}

fun <I, T: Identifiable<I>> Collection<T>.findById(id: I): T? = asSequence().find { it.getId() == id }

fun <I, T: Diffable<T, I, D>, D> Collection<T>.getDiffResults(those: Collection<T>): List<D> {
	return asSequence()
		.mapNotNull { oldItem ->
			val newItem = those.findById(oldItem.getId())
			if (newItem == null) {
				// no plan to addition nor deletion
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
fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
	return ofClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == ofClass } ?: ofClass
}
