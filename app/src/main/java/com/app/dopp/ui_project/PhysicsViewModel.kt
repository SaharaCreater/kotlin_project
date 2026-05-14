package com.app.dopp.ui_project

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.repository.PhysicsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicsViewModel @Inject constructor(
    private val repository: PhysicsRepository,
    private val scannerManager: ScannerManager
) : ViewModel() {

    val completedIds: StateFlow<Set<String>> = repository.getAllProgress()
        .map { list -> list.filter { it.isCompleted }.map { it.experimentId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val completedCount: StateFlow<Int> = repository.getAllProgress()
        .map { list -> list.count { it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        viewModelScope.launch {
            repository.seedLocalExperiments()
        }
    }

    fun markCompleted(experimentId: String) {
        viewModelScope.launch {
            repository.markCompleted(experimentId)
        }
    }

    fun onScanClick(navController: NavHostController) {
        scannerManager.startScanning { result ->
            result?.let { url ->
                navController.navigate("ar/${Uri.encode(url)}")
            }
        }
    }
}
