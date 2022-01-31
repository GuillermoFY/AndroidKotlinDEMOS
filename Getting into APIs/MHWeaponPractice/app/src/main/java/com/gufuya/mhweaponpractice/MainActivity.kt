package com.gufuya.mhweaponpractice

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.gufuya.mhweaponpractice.adapter.WeaponAdapter
import com.gufuya.mhweaponpractice.databinding.ActivityMainBinding
import com.gufuya.mhweaponpractice.models.Assets
import com.gufuya.mhweaponpractice.models.ElementStuff.ElementType
import com.gufuya.mhweaponpractice.models.Weapon
import com.gufuya.mhweaponpractice.models.WeaponElements
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var rvWeapons: RecyclerView

    var context: Context = this
    lateinit var wList: ArrayList<Weapon>
    lateinit var adapter: WeaponAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wList = ArrayList<Weapon>()

        rvWeapons = binding.rvWeapons
        rvWeapons.setHasFixedSize(true)
        rvWeapons.visibility = View.VISIBLE
        rvWeapons.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)

        adapter = WeaponAdapter(context, wList)
        rvWeapons.adapter = adapter

        getData()
    }

    fun getData(){
        wList.clear()
        val url = "https://mhw-db.com/weapons"

        val jor = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            {
                fun onResponse(response: JSONArray) = try{
                    for (i in 0 until response.length()) {
                        val weapon = response.getJSONObject(i)
                        var name: String = weapon.getString("name")
                        var type: String = weapon.getString("type")
                        var rarity: Int = weapon.getInt("rarity")
                        var assets: Assets? = null
                        try{
                            var assetsArr: JSONObject = weapon.getJSONObject("assets")
                            var icon = assetsArr.getString("icon")
                            icon = icon.replace("\\/","/")
                            var image = assetsArr.getString("image")
                            image = image.replace("\\/","/")
                            assets = Assets(icon,image)
                        }catch (e: Exception){
                            assets = Assets("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png","https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png")
                        }
                        /*
                        var elements: ArrayList<WeaponElements>? = null
                        try{
                            var elementsArr: JSONArray = weapon.getJSONArray("elements")
                            for(j in 0 until elementsArr.length()){
                                var element: JSONObject = elementsArr.getJSONObject(j)
                                var elementType: JSONObject = element.getJSONObject("type")
                                var name = elementType.getString("name")
                                //var name: String = element.getString("type")
                                var el = WeaponElements(ElementType(name))
                                elements?.add(el)
                            }
                        }catch (e: Exception){
                            elements?.add(WeaponElements(ElementType("NO ELEMENTS")))
                        }

                         */

                        var weap: Weapon = Weapon(name, type, Integer.valueOf(rarity), assets!!)
                        wList.add(weap)
                    }

                    adapter.notifyDataSetChanged()
                }catch (e:Exception){
                    e.printStackTrace();
                    Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
                onResponse(it)
            },
            {
                fun onErrorResponse(error: VolleyError){
                    Toast.makeText(context, "Error: " + error.message, Toast.LENGTH_LONG).show()
                }
                onErrorResponse(it)
            })


        var queue = Volley.newRequestQueue(context)
        queue.add(jor)
    }
}



