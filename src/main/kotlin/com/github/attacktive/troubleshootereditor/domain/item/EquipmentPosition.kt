package com.github.attacktive.troubleshootereditor.domain.item

import java.sql.Connection
import java.sql.PreparedStatement
import org.slf4j.LoggerFactory

enum class EquipmentPosition(val value: String) {
	WEAPON("Weapon"),
	BODY("Body"),
	HAND("Hand"),
	LEG("Leg"),
	INVENTORY1("Inventory1"),
	INVENTORY2("Inventory2"),
	GRENADEBAG("GrenadeBag"),
	COSTUME("Costume");

	fun cheatingStatements(connection: Connection): (Long) -> List<PreparedStatement> {
		return fun(id: Long): List<PreparedStatement> {
			val stringStatements: List<String> = when (this) {
				WEAPON -> defaults(id) + options(id, ItemOption.forWeapons)
				BODY -> defaults(id) + options(id, ItemOption.forBodies)
				HAND -> defaults(id) + options(id, ItemOption.forHands)
				LEG -> defaults(id) + options(id, ItemOption.forLegs)
				INVENTORY2 -> defaults(id) + options(id, ItemOption.forInventory2)
				COSTUME -> defaults(id) + options(id, ItemOption.forCostumes)
				else -> emptyList()
			}

			return stringStatements.map { statement -> connection.prepareStatement(statement) }
		}
	}

	companion object {
		private val logger = LoggerFactory.getLogger(EquipmentPosition::class.java)

		fun fromValue(value: String): EquipmentPosition {
			return entries.firstOrNull { it.value == value }
				?: throw IllegalArgumentException("No enum constant ${EquipmentPosition::class.java.canonicalName}.$value.")
		}

		private fun clearExisting(id: Long) = """
			delete from itemProperty
			where itemID = $id
		""".trimIndent()

		private fun secondHand(id: Long, isNew: Boolean = false) = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.IS_NEW.index}, '$isNew')
		""".trimIndent()

		private fun extreme(id: Long, optionKey: String = "Extreme") = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.OPTION_KEY.index}, '$optionKey')
		""".trimIndent()

		private fun bound(id: Long, isBound: Boolean = true) = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.BOUND.index}, '$isBound')
		""".trimIndent()

		private fun ratio(id: Long, ratio: Float = 1F) = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.RATIO.index}, '$ratio')
		""".trimIndent()

		private fun protected(id: Long, isProtected: Boolean = true) = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.PROTECTED.index}, '$isProtected')
		""".trimIndent()

		private fun level(id: Long, level: Int = 9) = """
			insert into itemProperty (itemID, masterIndex, propValue)
			values ($id, ${PropertyMaster.LEVEL.index}, '$level')
		""".trimIndent()

		private fun defaults(id: Long) = listOf(
			clearExisting(id),
			secondHand(id),
			extreme(id),
			bound(id),
			ratio(id),
			protected(id),
			level(id)
		)

		private fun options(id: Long, options: List<Pair<ItemOption, Int>>): List<String> {
			if (options.size > 5) {
				logger.warn("You can have up to 5 options! Other than the first five are going to be silently ignored.")
			}

			return options.mapIndexed { index, pair ->
				val nthOptions = PropertyMaster.getNthOptions(index + 1)

				listOf(
					"""
						insert into itemProperty (itemID, masterIndex, propValue)
						values ($id, ${nthOptions.first.index}, '${pair.first.value}')
					""".trimIndent(),
					"""
						insert into itemProperty (itemID, masterIndex, propValue)
						values ($id, ${nthOptions.second.index}, '${pair.second}')
					""".trimIndent()
				)
			}
			.flatten()
		}
	}
}
