package com.gufuya.bnpractice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//This is the class which receives the broadcast and send it to a local broadcast manager which is on the main class
class Receiver: BroadcastReceiver() {
    companion object{
        val RECIEVER_ACTION = Receiver::class.java.canonicalName + ".ACTION_RECEIVER"
    }

    override fun onReceive(context: Context, intent: Intent){
        //we get the string extra of the channel dataCopy (which is the one of the other app and send it to the LBM
        var msg = intent.getStringExtra("dataCopy")?.toString()
        if (msg!=null){
            val intent = Intent("msg")
            intent.putExtra("Msg", msg)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}