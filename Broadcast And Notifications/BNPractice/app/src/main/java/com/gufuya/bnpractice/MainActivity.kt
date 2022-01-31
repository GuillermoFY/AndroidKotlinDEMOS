package com.gufuya.bnpractice

import android.app.*
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import com.gufuya.bnpractice.adapter.RecyclerViewAdapter
import com.gufuya.bnpractice.databinding.ActivityMainBinding
import com.gufuya.bnpractice.model.Message
import com.gufuya.bnpractice.viewmodel.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var context: MainActivity
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private var messages : MutableList<Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view  = binding.root
        setContentView(view)

        context = this

        //Setting up the viewmodel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getAllMessages()

        //Setting up the adapter
        adapter = RecyclerViewAdapter(messages)

        //Setting up the recyclerview
        recyclerView = binding.rvMain
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        //Setting up the viewmodel observer
        viewModel.rvLiveData.observe(this){
            messages.clear()
            messages.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        }

        //Setting up the message receiver
        viewModel.setUpReceiver(context)

        binding.bSend.setOnClickListener {sM()}
    }

    private fun sM() {
        //If it's empty, will show a toast and if not we
        if(!binding.etMessage.text.toString().isEmpty()){
            //we create the message and send it to the viewmodel
            var msg = Message(binding.etMessage.text.toString(), Date(), true)
            viewModel.sendMessage(msg)
            //we hide the keyboard, set the text in blank and then scroll down to the last message
            hideKeyboard()
            binding.etMessage.setText("")
            recyclerView.scrollToPosition(messages.size - 1);
        } else {
            Toast.makeText(this, "The message must have content!", Toast.LENGTH_SHORT).show()
        }
    }

    //Hiding the keyboard
    private fun Context.hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}


