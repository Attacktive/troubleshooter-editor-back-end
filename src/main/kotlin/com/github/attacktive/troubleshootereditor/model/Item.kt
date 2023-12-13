package com.github.attacktive.troubleshootereditor.model

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory

data class Item(val id: Long, val type: String, val count: Long, val status: String, var equipmentPosition: EquipmentPosition? = null) {
	constructor(id: Long, type: String, count: Long, status: String, json: String, equipmentPosition: EquipmentPosition? = null): this(id, type, count, status, equipmentPosition) {
		properties = deserialize(json)
	}

	/**
	 * The property needs to be serialized so don't make it `private`.
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	var properties = mapOf<String, String>()

	companion object {
		private val logger = LoggerFactory.getLogger(Item::class.java)

		fun fromResultSet(resultSet: ResultSet): Item {
			val id = resultSet.getLong("itemID")
			val type = resultSet.getString("itemType")
			val count = resultSet.getLong("itemCount")
			val status = resultSet.getString("masterName")
			val properties = resultSet.getString("properties")
			var equipmentPosition: EquipmentPosition? = null
			try {
				val positionKey = resultSet.getString("positionKey")
				equipmentPosition = EquipmentPosition.fromValue(positionKey)
			} catch (_: SQLException) { }

			return Item(id, type, count, status, properties, equipmentPosition)
		}

		private fun deserialize(json: String): Map<String, String> {
			val objectMapper = jacksonObjectMapper()
			return objectMapper.readValue(json)
		}
	}

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
					COSTUME -> defaults(id) + options(id, ItemOption.forCostumes)
					else -> emptyList()
				}

				return stringStatements.map { statement -> connection.prepareStatement(statement) }
			}
		}

		companion object {
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

	private enum class PropertyMaster(val index: Int, val actualName: String) {
		IS_NEW(1, "IsNew"),
		OPTION_KEY(3, "Option/OptionKey"),
		TYPE1(4, "Option/Type1"),
		VALUE1(6, "Option/Value1"),
		TYPE2(5, "Option/Type2"),
		VALUE2(7, "Option/Value2"),
		TYPE3(166, "Option/Type3"),
		VALUE3(169, "Option/Value3"),
		TYPE4(307, "Option/Type4"),
		VALUE4(308, "Option/Value4"),
		TYPE5(3453, "Option/Type5"),
		VALUE5(3454, "Option/Value5"),
		BOUND(24, "Binded"),
		RATIO(56, "Option/Ratio"),
		PROTECTED(235, "Protected"),
		LEVEL(18518, "Lv");

		companion object {
			fun getNthOptions(n: Int) = when (n) {
				1 -> TYPE1 to VALUE1
				2 -> TYPE2 to VALUE2
				3 -> TYPE3 to VALUE3
				4 -> TYPE4 to VALUE4
				5 -> TYPE5 to VALUE5
				else -> throw IllegalArgumentException("The index must be between 1 and 5!")
			}
		}
	}

	private enum class ItemOption(val value: String) {
		ATTACK_POWER("AttackPower"),
		ESP_POWER("ESPPower"),
		CRITICAL_STRIKE_CHANCE("CriticalStrikeChance"),
		CRITICAL_STRIKE_DEAL("CriticalStrikeDeal"),
		OVERCHARGE_DURATION("OverchargeDuration"),
		ARMOR("Armor"),
		DODGE("Dodge"),
		BLOCK("Block"),
		RESISTANCE("Resistance"),
		ACCURACY("Accuracy"),
		SPEED("Speed"),
		MAX_HP("MaxHP"),
		MAX_VIGOR("MaxVigor"),
		MOVE_DISTANCE("MoveDist"),
		SIGHT_RANGE("SightRange"),
		SLASHING_RESISTANCE("SlashingResistance"),
		PIERCING_RESISTANCE("PiercingResistance"),
		BLUNT_RESISTANCE("BluntResistance"),
		FIRE_RESISTANCE("FireResistance"),
		ICE_RESISTANCE("IceResistance"),
		WATER_RESISTANCE("WaterResistance"),
		EARTH_RESISTANCE("EarthResistance"),
		WIND_RESISTANCE("WindResistance"),
		LIGHTNING_RESISTANCE("LightningResistance");

		companion object {
			val forWeapons = listOf(
				ATTACK_POWER to 10_000,
				ESP_POWER to 10_000,
				CRITICAL_STRIKE_CHANCE to 200,
				CRITICAL_STRIKE_DEAL to 500,
				OVERCHARGE_DURATION to 8
			)

			val forBodies = listOf(
				ARMOR to 10_000,
				DODGE to 200,
				BLOCK to 200,
				MAX_HP to 10_000,
				RESISTANCE to 10_000
			)

			val forHands = listOf(
				ACCURACY to 200,
				SIGHT_RANGE to 10,
				SLASHING_RESISTANCE to 10_000,
				PIERCING_RESISTANCE to 10_000,
				BLUNT_RESISTANCE to 10_000
			)

			val forLegs = listOf(
				MOVE_DISTANCE to 25,
				SPEED to 100,
				MAX_VIGOR to 100,
				FIRE_RESISTANCE to 10_000,
				ICE_RESISTANCE to 10_000
			)

			val forCostumes = listOf(
				WATER_RESISTANCE to 10_000,
				EARTH_RESISTANCE to 10_000,
				WIND_RESISTANCE to 10_000,
				LIGHTNING_RESISTANCE to 10_000,
				OVERCHARGE_DURATION to 1
			)
		}
	}
}
