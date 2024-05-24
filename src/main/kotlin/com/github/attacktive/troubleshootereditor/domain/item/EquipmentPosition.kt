package com.github.attacktive.troubleshootereditor.domain.item

@Suppress("unused")
enum class EquipmentPosition(val value: String, val options: List<Pair<ItemOption, Int>>?) {
	WEAPON("Weapon", ItemOption.forWeapons),
	BODY("Body", ItemOption.forBodies),
	HAND("Hand", ItemOption.forHands),
	LEG("Leg", ItemOption.forLegs),
	INVENTORY1("Inventory1", null),
	INVENTORY2("Inventory2", ItemOption.forInventory2),
	GRENADE_BAG("GrenadeBag", null),
	ALCHEMY_BAG("AlchemyBag", null),
	COSTUME("Costume", ItemOption.forCostumes)
}
