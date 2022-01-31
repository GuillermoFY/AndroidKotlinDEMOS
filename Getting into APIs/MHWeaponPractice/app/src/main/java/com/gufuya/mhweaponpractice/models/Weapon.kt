package com.gufuya.mhweaponpractice.models


data class Weapon(
    var name: String,
    var type: String,
    var rarity: Int,
    //val elements: ArrayList<WeaponElements>,
    val assets: Assets
)
