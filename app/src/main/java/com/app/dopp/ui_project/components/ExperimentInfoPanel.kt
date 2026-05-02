package com.app.dopp.ui_project.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dopp.physics.*
import kotlin.math.abs

/**
 * Panel showing real-time experiment data and physics values
 */
@Composable
fun ExperimentInfoPanel(
    experimentType: ExperimentType,
    // States
    pendulumState: PendulumState? = null,
    freeFallState: FreeFallState? = null,
    collisionState: CollisionState? = null,
    circuitState: CircuitState? = null,
    magneticFieldState: MagneticFieldState? = null,
    refractionState: RefractionState? = null,
    lensState: LensState? = null,
    brownianState: BrownianMotionState? = null,
    gasExpansionState: GasExpansionState? = null,
    // Parameters for context
    pendulumParams: PendulumParameters? = null,
    freeFallParams: FreeFallParameters? = null,
    collisionParams: CollisionParameters? = null,
    circuitParams: CircuitParameters? = null,
    refractionParams: RefractionParameters? = null,
    lensParams: LensParameters? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Данные эксперимента",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            
            when (experimentType) {
                ExperimentType.PENDULUM -> pendulumState?.let { state ->
                    PendulumInfo(state, pendulumParams)
                }
                ExperimentType.FREE_FALL -> freeFallState?.let { state ->
                    FreeFallInfo(state, freeFallParams)
                }
                ExperimentType.COLLISION -> collisionState?.let { state ->
                    CollisionInfo(state, collisionParams)
                }
                ExperimentType.ELECTRIC_CIRCUIT -> circuitState?.let { state ->
                    CircuitInfo(state, circuitParams)
                }
                ExperimentType.MAGNETIC_FIELD -> magneticFieldState?.let { state ->
                    MagneticFieldInfo(state)
                }
                ExperimentType.LIGHT_REFRACTION -> refractionState?.let { state ->
                    RefractionInfo(state, refractionParams)
                }
                ExperimentType.LENS -> lensState?.let { state ->
                    LensInfo(state, lensParams)
                }
                ExperimentType.BROWNIAN_MOTION -> brownianState?.let { state ->
                    BrownianInfo(state)
                }
                ExperimentType.GAS_EXPANSION -> gasExpansionState?.let { state ->
                    GasExpansionInfo(state)
                }
            }
        }
    }
}

@Composable
private fun PendulumInfo(
    state: PendulumState,
    params: PendulumParameters?
) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Текущий угол", "%.1f°".format(Math.toDegrees(state.currentAngle.toDouble())))
    InfoRow("Угловая скорость", "%.2f рад/с".format(state.angularVelocity))
    InfoRow("Период", "%.2f с".format(state.period))
    
    Spacer(modifier = Modifier.height(4.dp))
    
    Text(
        text = "Энергия",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    InfoRow("Кинетическая", "%.3f Дж".format(state.kineticEnergy))
    InfoRow("Потенциальная", "%.3f Дж".format(state.potentialEnergy))
    InfoRow("Полная", "%.3f Дж".format(state.kineticEnergy + state.potentialEnergy))
}

@Composable
private fun FreeFallInfo(
    state: FreeFallState,
    params: FreeFallParameters?
) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Высота", "%.2f м".format(state.currentHeight))
    InfoRow("Скорость", "%.2f м/с".format(abs(state.currentVelocity)))
    InfoRow("Макс. скорость", "%.2f м/с".format(state.maxVelocity))
    
    if (state.hasLanded) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Приземление!",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
    
    params?.let {
        Spacer(modifier = Modifier.height(4.dp))
        val theoreticalTime = PhysicsCalculations.fallTime(it.initialHeight)
        InfoRow("Теоретическое время падения", "%.2f с".format(theoreticalTime))
    }
}

@Composable
private fun CollisionInfo(
    state: CollisionState,
    params: CollisionParameters?
) {
    InfoRow("Время", "%.2f с".format(state.time))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Шар 1",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    InfoRow("Позиция", "%.2f м".format(state.position1))
    InfoRow("Скорость", "%.2f м/с".format(state.currentVelocity1))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Шар 2",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary
    )
    InfoRow("Позиция", "%.2f м".format(state.position2))
    InfoRow("Скорость", "%.2f м/с".format(state.currentVelocity2))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Законы сохранения",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.tertiary
    )
    InfoRow("Импульс до", "%.2f кг·м/с".format(state.totalMomentumBefore))
    InfoRow("Импульс после", "%.2f кг·м/с".format(state.totalMomentumAfter))
    InfoRow("Энергия до", "%.2f Дж".format(state.totalEnergyBefore))
    InfoRow("Энергия после", "%.2f Дж".format(state.totalEnergyAfter))
    
    if (state.hasCollided) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (params?.isElastic == true) "Упругое столкновение" else "Неупругое столкновение",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun CircuitInfo(
    state: CircuitState,
    params: CircuitParameters?
) {
    InfoRow("Общее сопротивление", "%.1f Ω".format(state.totalResistance))
    InfoRow("Общий ток", "%.3f А".format(state.totalCurrent))
    InfoRow("Мощность", "%.2f Вт".format(state.power))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Напряжения",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    InfoRow("U₁", "%.2f В".format(state.voltage1))
    InfoRow("U₂", "%.2f В".format(state.voltage2))
    InfoRow("U₃", "%.2f В".format(state.voltage3))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Токи",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.secondary
    )
    InfoRow("I₁", "%.3f А".format(state.current1))
    InfoRow("I₂", "%.3f А".format(state.current2))
    InfoRow("I₃", "%.3f А".format(state.current3))
}

@Composable
private fun MagneticFieldInfo(state: MagneticFieldState) {
    InfoRow("Время", "%.2f с".format(state.time))
    
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Напряжённость поля B",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    
    state.fieldStrengthAtDistance.forEach { (distance, field) ->
        InfoRow("r = %.0f см".format(distance * 100), "%.2e Тл".format(field))
    }
}

@Composable
private fun RefractionInfo(
    state: RefractionState,
    params: RefractionParameters?
) {
    params?.let {
        InfoRow("Угол падения", "%.1f°".format(Math.toDegrees(it.incidentAngle.toDouble())))
        InfoRow("n₁", "%.2f".format(it.n1))
        InfoRow("n₂", "%.2f".format(it.n2))
    }
    
    Spacer(modifier = Modifier.height(4.dp))
    
    if (state.isTotalInternalReflection) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Полное внутреннее отражение",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        state.criticalAngle?.let {
            InfoRow("Критический угол", "%.1f°".format(Math.toDegrees(it.toDouble())))
        }
    } else {
        state.refractedAngle?.let {
            InfoRow("Угол преломления", "%.1f°".format(Math.toDegrees(it.toDouble())))
        }
    }
}

@Composable
private fun LensInfo(
    state: LensState,
    params: LensParameters?
) {
    params?.let {
        InfoRow("Фокусное расстояние", "%.2f м".format(it.focalLength))
        InfoRow("Расстояние до объекта", "%.2f м".format(it.objectDistance))
    }
    
    Spacer(modifier = Modifier.height(4.dp))
    
    if (state.imageDistance.isInfinite()) {
        Text(
            text = "Изображение на бесконечности",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        InfoRow("Расстояние до изображения", "%.2f м".format(state.imageDistance))
        InfoRow("Высота изображения", "%.2f м".format(state.imageHeight))
        InfoRow("Увеличение", "%.2fx".format(abs(state.magnification)))
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.isVirtual) {
                InfoChip("Мнимое")
            } else {
                InfoChip("Действительное")
            }
            if (state.isInverted) {
                InfoChip("Перевёрнутое")
            } else {
                InfoChip("Прямое")
            }
        }
    }
}

@Composable
private fun BrownianInfo(state: BrownianMotionState) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Число частиц", "${state.particlePositions.size}")
    
    // Average displacement
    if (state.particleTrails.isNotEmpty() && state.particleTrails[0].size > 1) {
        val firstTrail = state.particleTrails[0]
        val startPos = firstTrail.first()
        val endPos = firstTrail.last()
        val displacement = kotlin.math.sqrt(
            (endPos.first - startPos.first) * (endPos.first - startPos.first) +
            (endPos.second - startPos.second) * (endPos.second - startPos.second)
        )
        InfoRow("Среднее смещение", "%.4f".format(displacement))
    }
}

@Composable
private fun GasExpansionInfo(state: GasExpansionState) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Температура", "%.0f K (%.0f°C)".format(state.currentTemperature, state.currentTemperature - 273f))
    InfoRow("Объём", "%.2f V₀".format(state.currentVolume))
    InfoRow("Давление", "%.0f Па".format(state.currentPressure))
    
    if (state.moleculeSpeeds.isNotEmpty()) {
        val avgSpeed = state.moleculeSpeeds.average()
        InfoRow("Средняя скорость молекул", "%.0f м/с".format(avgSpeed))
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
