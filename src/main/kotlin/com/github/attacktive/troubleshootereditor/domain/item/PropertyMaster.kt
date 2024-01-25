package com.github.attacktive.troubleshootereditor.domain.item

enum class PropertyMaster(val index: Int) {
	/**
	 * Property key: IsNew
	 */
	IS_NEW(1),

	/**
	 * Property key: Option/OptionKey
	 */
	OPTION_KEY(3),

	/**
	 * Property key: Option/Type1
	 */
	TYPE1(4),

	/**
	 * Property key: Option/Value1
	 */
	VALUE1(6),

	/**
	 * Property key: Option/Type2
	 */
	TYPE2(5),

	/**
	 * Property key: Option/Value2
	 */
	VALUE2(7),

	/**
	 * Property key: Option/Type3
	 */
	TYPE3(166),

	/**
	 * Property key: Option/Value3
	 */
	VALUE3(169),

	/**
	 * Property key: Option/Type4
	 */
	TYPE4(307),

	/**
	 * Property key: Option/Value4
	 */
	VALUE4(308),

	/**
	 * Property key: Option/Type5
	 */
	TYPE5(3453),

	/**
	 * Property key: Option/Value5
	 */
	VALUE5(3454),

	/**
	 * Property key: Binded
	 */
	BOUND(24),

	/**
	 * Property key: Option/Ratio
	 */
	RATIO(56),

	/**
	 * Property key: Protected
	 */
	PROTECTED(235),

	/**
	 * Property key: Lv
	 */
	LEVEL(18518);

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
