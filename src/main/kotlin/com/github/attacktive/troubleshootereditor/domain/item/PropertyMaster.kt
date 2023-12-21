package com.github.attacktive.troubleshootereditor.domain.item

enum class PropertyMaster(val index: Int, val actualName: String) {
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
