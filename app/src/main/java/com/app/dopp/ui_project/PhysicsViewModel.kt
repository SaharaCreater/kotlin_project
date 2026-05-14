package com.app.dopp.ui_project

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.repository.PhysicsRepository
import com.app.dopp.domain.PhysicsExperiment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicsViewModel @Inject constructor(
    private val repository: PhysicsRepository,
    private val scannerManager: ScannerManager
) : ViewModel() {

    private val _experiments = MutableStateFlow<List<PhysicsExperiment>>(emptyList())
    val experiments: StateFlow<List<PhysicsExperiment>> = _experiments.asStateFlow()

    val isOffline: StateFlow<Boolean> = repository.networkMonitor.isOnline
        .map { !it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.refreshExperiments()
            repository.getExperimentsFromDb().collect { list ->
                _experiments.value = list
            }
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
