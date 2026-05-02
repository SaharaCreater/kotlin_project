package com.app.dopp.ui_project

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.app.dopp.data.ScannerManager // Проверь правильность этого импорта
import com.app.dopp.data.repository.PhysicsRepository
import com.app.dopp.domain.PhysicsExperiment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicsViewModel @Inject constructor(
    private val repository: PhysicsRepository,
    private val scannerManager: ScannerManager // ДОБАВЛЕНО: Теперь ViewModel знает о сканере
) : ViewModel() {

    private val _experiments = MutableStateFlow<List<PhysicsExperiment>>(emptyList())
    val experiments: StateFlow<List<PhysicsExperiment>> = _experiments.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Сначала запускаем обновление из сети (Requirement 5)
            repository.refreshExperiments()
            // Затем подписываемся на данные из БД (Requirement 4)
            repository.getExperimentsFromDb().collect { list ->
                _experiments.value = list
            }
        }
    }

    fun onScanClick(navController: NavHostController) {
        // Теперь scannerManager доступен, так как он передан через конструктор
        scannerManager.startScanning { result ->
            result?.let { url ->
                // Переходим в AR, используя полученную из QR ссылку
                navController.navigate("ar/${Uri.encode(url)}")
            }
        }
    }
}