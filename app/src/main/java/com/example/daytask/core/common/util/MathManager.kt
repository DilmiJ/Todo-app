package com.example.daytask.core.common.util

import com.example.daytask.data.model.SubTask

object MathManager {
    fun countCompletePercentage(subTasksList: List<SubTask>): Float =
        if (subTasksList.isEmpty()) 0f
        else subTasksList.sumOf { it.completed.compareTo(false) } / subTasksList.size.toFloat()
}