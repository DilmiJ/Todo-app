package com.example.daytask.data.model

data class Task(
    val id: String = "",
    val title: String = "",
    val detail: String = "",
    val memberList: List<User> = listOf(),
    val date: Long = 0L,
    val subTasksList: List<SubTask> = listOf(),
    val taskComplete: Boolean = false
)

