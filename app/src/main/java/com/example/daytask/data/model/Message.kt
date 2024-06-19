package com.example.daytask.data.model

data class Message(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val isRead: Boolean
)