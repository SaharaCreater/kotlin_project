package com.app.dopp.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiment_progress")
data class ExperimentProgress(
    @PrimaryKey val experimentId: String,
    val isCompleted: Boolean = false,
    val openCount: Int = 0,
    val completedAt: Long? = null,
    val pendingSyncNeeded: Boolean = false
)
