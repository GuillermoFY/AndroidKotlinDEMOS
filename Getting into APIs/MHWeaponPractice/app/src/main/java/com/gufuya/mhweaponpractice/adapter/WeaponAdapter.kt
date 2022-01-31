package com.gufuya.mhweaponpractice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gufuya.mhweaponpractice.R
import com.gufuya.mhweaponpractice.models.Weapon
import com.gufuya.mhweaponpractice.viewholder.WeaponViewHolder

class WeaponAdapter(val context: Context, val wList: List<Weapon>):
    RecyclerView.Adapter<WeaponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WeaponViewHolder(inflater.inflate(R.layout.weapon_item,parent,false))
    }

    override fun getItemCount(): Int = wList.size

    override fun onBindViewHolder(holder: WeaponViewHolder, position: Int) {
        val weapon = wList[position]
        holder.bind(weapon)
    }
}

