package com.gufuya.mhweaponpractice.viewholder

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gufuya.mhweaponpractice.databinding.WeaponItemBinding
import com.gufuya.mhweaponpractice.models.Weapon
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.bumptech.glide.request.RequestOptions
import com.gufuya.mhweaponpractice.models.ElementStuff.ElementType
import com.gufuya.mhweaponpractice.models.WeaponElements


class WeaponViewHolder(view: View): RecyclerView.ViewHolder(view){
    private val binding = WeaponItemBinding.bind(view)
    private val view: View = view

    var tvTitle: TextView = binding.tvTitle
    var tvType: TextView = binding.tvType
    var tvRarity: TextView = binding.tvRarity
    var ivWeapon: ImageView = binding.ivWeapon
    var ivType: ImageView = binding.ivType
    var tvElements: TextView = binding.tvElements

    fun bind(weapon: Weapon){
        tvTitle.text = weapon.name
        tvType.text = weapon.type
        tvRarity.text = weapon.rarity.toString()

        Glide.with(view).load(weapon.assets.image).fitCenter().override(350,350).into(ivWeapon)
        Glide.with(view).load(weapon.assets.icon).fitCenter().override(50,50).into(ivType)

        /*
        var elements = ""
        for(i in 0 until weapon.elements.size){
            var we: WeaponElements = weapon.elements.get(i)
            var et: ElementType = we.type
            var elementName = et.name
            elements+=" $elementName "
        }
         */
    }
}