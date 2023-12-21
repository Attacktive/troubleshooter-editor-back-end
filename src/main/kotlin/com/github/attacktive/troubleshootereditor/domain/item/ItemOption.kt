package com.github.attacktive.troubleshootereditor.domain.item

enum class ItemOption(val value: String) {
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
	MAX_SP("MaxAddSP"),
	VIGOR_REGENERATION("RegenVigor"),
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

		val forInventory2 = listOf(
			ATTACK_POWER to 1_000,
			ESP_POWER to 1_000,
			ACCURACY to 50,
			VIGOR_REGENERATION to 10,
			MAX_SP to 10
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
