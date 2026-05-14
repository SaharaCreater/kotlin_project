package com.app.dopp.ui_project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dopp.physics.*
import kotlin.math.abs

private val InfoBg = Color(0xFF0D0821).copy(alpha = 0.88f)
private val InfoBorder = Color.White.copy(alpha = 0.10f)
private val InfoLabel = Color.White.copy(alpha = 0.50f)
private val InfoValue = Color(0xFF93C5FD)
private val InfoAccent = Color(0xFF7C3AED)
private val InfoDivider = Color.White.copy(alpha = 0.07f)

@Composable
fun ExperimentInfoPanel(
    experimentType: ExperimentType,
    pendulumState: PendulumState? = null,
    freeFallState: FreeFallState? = null,
    collisionState: CollisionState? = null,
    circuitState: CircuitState? = null,
    magneticFieldState: MagneticFieldState? = null,
    refractionState: RefractionState? = null,
    lensState: LensState? = null,
    brownianState: BrownianMotionState? = null,
    gasExpansionState: GasExpansionState? = null,
    pendulumParams: PendulumParameters? = null,
    freeFallParams: FreeFallParameters? = null,
    collisionParams: CollisionParameters? = null,
    circuitParams: CircuitParameters? = null,
    refractionParams: RefractionParameters? = null,
    lensParams: LensParameters? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(InfoBg)
            .border(1.dp, InfoBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(3.dp, 14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(listOf(InfoAccent, Color(0xFF0EA5E9)))
                        )
                )
                Text(
                    text = "Данные",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            HorizontalDivider(color = InfoDivider)

            when (experimentType) {
                ExperimentType.PENDULUM -> pendulumState?.let { PendulumInfo(it, pendulumParams) }
                ExperimentType.FREE_FALL -> freeFallState?.let { FreeFallInfo(it, freeFallParams) }
                ExperimentType.COLLISION -> collisionState?.let { CollisionInfo(it, collisionParams) }
                ExperimentType.ELECTRIC_CIRCUIT -> circuitState?.let { CircuitInfo(it, circuitParams) }
                ExperimentType.MAGNETIC_FIELD -> magneticFieldState?.let { MagneticFieldInfo(it) }
                ExperimentType.LIGHT_REFRACTION -> refractionState?.let { RefractionInfo(it, refractionParams) }
                ExperimentType.LENS -> lensState?.let { LensInfo(it, lensParams) }
                ExperimentType.BROWNIAN_MOTION -> brownianState?.let { BrownianInfo(it) }
                ExperimentType.GAS_EXPANSION -> gasExpansionState?.let { GasExpansionInfo(it) }
            }
        }
    }
}

@Composable
private fun InfoSectionLabel(text: String, color: Color = InfoAccent) {
    Row(
        modifier = Modifier.padding(top = 4.dp, bottom = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun PendulumInfo(state: PendulumState, params: PendulumParameters?) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Текущий угол", "%.1f°".format(Math.toDegrees(state.currentAngle.toDouble())))
    InfoRow("Угловая скорость", "%.2f рад/с".format(state.angularVelocity))
    InfoRow("Период", "%.2f с".format(state.period))
    InfoSectionLabel("Энергия", Color(0xFF34D399))
    InfoRow("Кинетическая", "%.3f Дж".format(state.kineticEnergy))
    InfoRow("Потенциальная", "%.3f Дж".format(state.potentialEnergy))
    InfoRow("Полная", "%.3f Дж".format(state.kineticEnergy + state.potentialEnergy))
}

@Composable
private fun FreeFallInfo(state: FreeFallState, params: FreeFallParameters?) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Высота", "%.2f м".format(state.currentHeight))
    InfoRow("Скорость", "%.2f м/с".format(abs(state.currentVelocity)))
    InfoRow("Макс. скорость", "%.2f м/с".format(state.maxVelocity))
    if (state.hasLanded) {
        StatusBadge("Приземление!", Color(0xFF34D399))
    }
    params?.let {
        val theoreticalTime = PhysicsCalculations.fallTime(it.initialHeight)
        InfoRow("Теор. время падения", "%.2f с".format(theoreticalTime))
    }
}

@Composable
private fun CollisionInfo(state: CollisionState, params: CollisionParameters?) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoSectionLabel("Шар 1", InfoAccent)
    InfoRow("Позиция", "%.2f м".format(state.position1))
    InfoRow("Скорость", "%.2f м/с".format(state.currentVelocity1))
    InfoSectionLabel("Шар 2", Color(0xFF0EA5E9))
    InfoRow("Позиция", "%.2f м".format(state.position2))
    InfoRow("Скорость", "%.2f м/с".format(state.currentVelocity2))
    InfoSectionLabel("Законы сохранения", Color(0xFFF59E0B))
    InfoRow("Импульс до", "%.2f кг·м/с".format(state.totalMomentumBefore))
    InfoRow("Импульс после", "%.2f кг·м/с".format(state.totalMomentumAfter))
    InfoRow("Энергия до", "%.2f Дж".format(state.totalEnergyBefore))
    InfoRow("Энергия после", "%.2f Дж".format(state.totalEnergyAfter))
    if (state.hasCollided) {
        val label = if (params?.isElastic == true) "Упругое столкновение" else "Неупругое"
        StatusBadge(label, Color(0xFFF59E0B))
    }
}

@Composable
private fun CircuitInfo(state: CircuitState, params: CircuitParameters?) {
    InfoRow("Общее сопротивление", "%.1f Ω".format(state.totalResistance))
    InfoRow("Общий ток", "%.3f А".format(state.totalCurrent))
    InfoRow("Мощность", "%.2f Вт".format(state.power))
    InfoSectionLabel("Напряжения", InfoAccent)
    InfoRow("U₁", "%.2f В".format(state.voltage1))
    InfoRow("U₂", "%.2f В".format(state.voltage2))
    InfoRow("U₃", "%.2f В".format(state.voltage3))
    InfoSectionLabel("Токи", Color(0xFF0EA5E9))
    InfoRow("I₁", "%.3f А".format(state.current1))
    InfoRow("I₂", "%.3f А".format(state.current2))
    InfoRow("I₃", "%.3f А".format(state.current3))
}

@Composable
private fun MagneticFieldInfo(state: MagneticFieldState) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoSectionLabel("Напряжённость поля B", Color(0xFF0EA5E9))
    state.fieldStrengthAtDistance.forEach { (distance, field) ->
        InfoRow("r = %.0f см".format(distance * 100), "%.2e Тл".format(field))
    }
}

@Composable
private fun RefractionInfo(state: RefractionState, params: RefractionParameters?) {
    params?.let {
        InfoRow("Угол падения", "%.1f°".format(Math.toDegrees(it.incidentAngle.toDouble())))
        InfoRow("n₁", "%.2f".format(it.n1))
        InfoRow("n₂", "%.2f".format(it.n2))
    }
    if (state.isTotalInternalReflection) {
        StatusBadge("Полное внутреннее отражение", Color(0xFFEF4444))
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
private fun LensInfo(state: LensState, params: LensParameters?) {
    params?.let {
        InfoRow("Фокусное расстояние", "%.2f м".format(it.focalLength))
        InfoRow("Расст. до объекта", "%.2f м".format(it.objectDistance))
    }
    if (state.imageDistance.isInfinite()) {
        StatusBadge("Изображение на ∞", Color(0xFF0EA5E9))
    } else {
        InfoRow("Расст. до изображения", "%.2f м".format(state.imageDistance))
        InfoRow("Высота изображения", "%.2f м".format(state.imageHeight))
        InfoRow("Увеличение", "%.2fx".format(abs(state.magnification)))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 2.dp)) {
            MiniChip(if (state.isVirtual) "Мнимое" else "Действительное")
            MiniChip(if (state.isInverted) "Перевёрнутое" else "Прямое")
        }
    }
}

@Composable
private fun BrownianInfo(state: BrownianMotionState) {
    InfoRow("Время", "%.2f с".format(state.time))
    InfoRow("Число частиц", "${state.particlePositions.size}")
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
    InfoRow("Температура", "%.0f K".format(state.currentTemperature))
    InfoRow("Объём", "%.2f V₀".format(state.currentVolume))
    InfoRow("Давление", "%.0f Па".format(state.currentPressure))
    if (state.moleculeSpeeds.isNotEmpty()) {
        InfoRow("Средн. скорость молекул", "%.0f м/с".format(state.moleculeSpeeds.average()))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = InfoLabel,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = InfoValue,
            fontSize = 11.sp,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun MiniChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = InfoLabel,
            fontSize = 10.sp
        )
    }
}
