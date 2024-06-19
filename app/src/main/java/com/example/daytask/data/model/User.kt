package com.example.daytask.data.model


data class User(
    val userId: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isOnline: Boolean = false,
)