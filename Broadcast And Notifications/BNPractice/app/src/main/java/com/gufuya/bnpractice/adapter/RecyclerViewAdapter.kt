package com.gufuya.bnpractice.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gufuya.bnpractice.R
import com.gufuya.bnpractice.model.Message
import java.text.SimpleDateFormat

class RecyclerViewAdapter(val msgList: List<Message>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //Here, we create a nested class of the viewholder to get all the main view content that we need
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvMessage: TextView
        var tvDate: TextView
        var lLayout: LinearLayout
        init {
            // Define click listener for the ViewHolder's View.
            tvMessage = view.findViewById(R.id.tvMessage)
            tvDate = view.findViewById(R.id.tvDate)
            lLayout = view.findViewById(R.id.lLayout)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.message, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var msg: Message = msgList.get(position)
        val vh: ViewHolder = viewHolder
        vh.tvMessage.text = msg.message

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val endDate = dateFormat.format(msg.date)
        vh.tvDate.text = endDate

        if(msg.self){
            vh.tvMessage.gravity = Gravity.RIGHT
            vh.tvDate.gravity = Gravity.RIGHT
        } else {
            vh.tvMessage.gravity = Gravity.LEFT
            vh.tvDate.gravity = Gravity.LEFT
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = msgList.size

}