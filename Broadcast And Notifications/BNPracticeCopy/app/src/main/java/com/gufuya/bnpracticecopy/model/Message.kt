package com.gufuya.bnpracticecopy.model

import java.util.*

//SIMPLE DATA CLASS FOR THE MESSAGES
/*
The bool of self is for knowing if the message is sended by this application if it's True and False if we receive the message
 */
data class Message(val message: String, val date: Date, val self: Boolean)