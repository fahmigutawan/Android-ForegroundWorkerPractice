package com.example.workerpractice.model

data class SingleTodoResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
