package com.gufuya.bnpractice.viewmodel

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.gufuya.bnpractice.MainActivity
import com.gufuya.bnpractice.R
import com.gufuya.bnpractice.receiver.Receiver
import com.gufuya.bnpractice.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val NOTIFICATION_CHANNEL_ID = "ID_Notification_Channel"
const val NOTIFICATION_CHANNEL_NAME = "Get Notifications"
const val NOTIFICATION_ID = 1
const val PENDING_INTENT_REQUEST = 1

//The ViewModel is the one that gets and sends the messages and put it into the recycler view with the live data
//It extends from AndroidViewModel which is a viewmodel but with the application activity, so we can get the context
//And also we get and send notifications
class MainViewModel(application: Application): AndroidViewModel(application) {

    //Get the context
    private val context = getApplication<Application>().applicationContext

    val pendingIntent = makePendingIntent()

    //Create the live data and the list of messages
    val rvLiveData: MutableLiveData<MutableList<Message>> = MutableLiveData()
    var msgList: ArrayList<Message> = ArrayList<Message>()

    //Function to send message and add it to the list and livedata
    fun sendMessage(msg:Message){
        sBroadcast(msg.message, context)
        msgList.add(msg)
        rvLiveData.postValue(msgList)
    }

    //Function to get all messages
    fun getAllMessages(){
        CoroutineScope(Dispatchers.IO).launch{
            rvLiveData.postValue(msgList)
        }
    }

    init{
        rvLiveData.value = msgList
    }

    //BROADCAST FUNCTIONS

    //In this function we will send the broadcast intent with the message string
    fun sBroadcast(msg: String,context: Context){
        var message = msg
        Intent().also { intent ->
            intent.setAction("com.pkg.perform.Ruby")
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            intent.putExtra("data", message)
            context.sendBroadcast(intent)
        }
    }

    /*
    Now, we have to set up the Receiver, getting the receiver class, creating a new intent filter
    and reciever with the LocalBroadcastManager the intents, then we put at the msgReciever the
    message we have recieved and add it to the lv and the list
    */
    fun setUpReceiver(context: Context){
        val receiver = Receiver()
        val intentFilter = IntentFilter("com.pkg.perform.Ruby")
        if (intentFilter != null) {
            context.registerReceiver(receiver, intentFilter)
        }
        val f = IntentFilter(Receiver.RECIEVER_ACTION)
        context.registerReceiver(receiver,f)

        LocalBroadcastManager.getInstance(context).registerReceiver(
            msgReceiver,
            IntentFilter("msg")
        )
    }

    private val msgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra("Msg")
            var msg = Message(message.toString(), Date(),false)
            msgList.add(msg)
            rvLiveData.postValue(msgList)
            //Send notification
            sNotifications(msg.message)
        }
    }

    //NOTIFICATION FUNCTIONS

    //In this function we will send the notification with a coroutine creating the channel and and the notification
    private fun sNotifications(s: String?) {
        CoroutineScope(Dispatchers.Default).launch {
            createChannel()
            createNotification(s)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Creating the channel object
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            //Creating the manager
            val manager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            channel.setShowBadge(true)

            //Set the channel with the manager
            manager.createNotificationChannel(channel)
        }
    }

    //Function to create the notification
    private fun createNotification(msg: String?) {
        //We create the builder with the context and the id of the channel
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

        with(builder) {
            //Set the foreground launcher icon for the icon of the notification
            setSmallIcon(R.drawable.ic_launcher_background)

            //Set the tittle of the name
            setContentTitle("Copied Application")

            //Setting the message text
            setStyle(NotificationCompat.BigTextStyle()
                .bigText(msg))

            color = Color.MAGENTA
            priority = NotificationCompat.PRIORITY_DEFAULT

            setDefaults(Notification.DEFAULT_SOUND)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            //Cancels notification after 30 sec
            setTimeoutAfter(30000L)

            //When the notification is clicked, it disappears
            setAutoCancel(true)

            //1 sec on 1 sec off
            setLights(Color.MAGENTA, 1000, 1000)

            //1 sec on 1 sec off 1 sec on 1 sec off
            setVibrate(longArrayOf(1000, 1000, 1000, 1000))

            //When we click on the notification
            setContentIntent(pendingIntent)

            //See full intent
            setFullScreenIntent(pendingIntent, true)
        }

        //Creating the notification
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        //Launch the notification
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    //When we click the notification it opens the application intent, so we have to make it
    private fun makePendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, PENDING_INTENT_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}