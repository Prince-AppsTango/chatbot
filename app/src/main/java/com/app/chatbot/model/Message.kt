package com.app.chatbot.model

data class Message(
    val text: String,
    val isUser: Boolean,
    val side : String,
    val isLoading: Boolean = false,
    val timeStamp : Long = System.currentTimeMillis()
)