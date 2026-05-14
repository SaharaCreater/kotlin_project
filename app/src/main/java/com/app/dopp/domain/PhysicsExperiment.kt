package com.app.dopp.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiments")
data class PhysicsExperiment(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val modelUrl: String? = null,
    val title: String? = null,
    val icon: String? = null,
    val difficulty: String? = null
)