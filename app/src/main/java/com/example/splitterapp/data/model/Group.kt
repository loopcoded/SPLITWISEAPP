package com.example.splitterapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val participants: List<String> = emptyList(),
    val title: String = "",
    val amountPaid: Double = 0.0,
    val paidBy: String = "",
    val date: String = "",
    val discount: Double = 0.0,
    val gst: Double = 0.0
)