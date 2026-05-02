package com.app.dopp.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiments")
data class PhysicsExperiment(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val modelUrl: String, // Ссылка на .glb файл для AR
    val category: String,
    val title: String
)