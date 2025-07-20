package com.example.lotteryprediction.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_methods")
data class PredictionMethod(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val algorithm: String,
    val version: Int = 0
)