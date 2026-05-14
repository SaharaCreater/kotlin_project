package com.app.dopp.ui_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.app.dopp.data.NetworkMonitor
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.repository.PhysicsRepository
import com.app.dopp.physics.ExperimentCategory
import com.app.dopp.physics.ExperimentType
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
    private val scannerManager: ScannerManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val completedIds: StateFlow<Set<String>> = repository.getAllProgress()
        .map { list -> list.filter { it.isCompleted }.map { it.experimentId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val completedCount: StateFlow<Int> = repository.getAllProgress()
        .map { list -> list.count { it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val totalRunCount: StateFlow<Int> = repository.getAllProgress()
        .map { list -> list.sumOf { it.openCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val progressByCategory: StateFlow<Map<ExperimentCategory, Pair<Int, Int>>> =
        repository.getAllProgress()
            .map { list ->
                val completedSet = list.filter { it.isCompleted }.map { it.experimentId }.toSet()
                ExperimentCategory.entries.associateWith { cat ->
                    val catExps = ExperimentType.entries.filter { it.category == cat }
                    val done = catExps.count { completedSet.contains(it.name) }
                    Pair(done, catExps.size)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init {
        viewModelScope.launch {
            repository.seedLocalExperiments()
            repository.syncProgressFromRemote()
        }
        viewModelScope.launch {
            var wasOffline = false
            networkMonitor.isOnline.collect { online ->
                if (online && wasOffline) {
                    repository.syncPendingToRemote()
                    repository.syncProgressFromRemote()
                }
                wasOffline = !online
            }
        }
    }

    fun markCompleted(experimentId: String) {
        viewModelScope.launch {
            repository.markCompleted(experimentId)
        }
    }

    fun onScanClick(navController: NavHostController) {
        scannerManager.startScanning { result ->
            result?.let { raw ->
                val experimentId = extractExperimentId(raw)
                if (experimentId != null) {
                    navController.navigate("ar/$experimentId")
                }
            }
        }
    }

    private fun extractExperimentId(raw: String): String? {
        val upperRaw = raw.uppercase()
        val match = ExperimentType.entries.firstOrNull { upperRaw.contains(it.name) }
        if (match != null) return match.name
        if (ExperimentType.entries.any { it.name == raw }) return raw
        return null
    }
}
