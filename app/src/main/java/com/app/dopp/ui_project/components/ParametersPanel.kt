package com.app.dopp.ui_project.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dopp.physics.*
import kotlin.math.roundToInt

private val PanelBg = Color(0xFF120D27)
private val AccentViolet = Color(0xFF7C3AED)
private val AccentSky = Color(0xFF0EA5E9)
private val LabelColor = Color.White.copy(alpha = 0.55f)
private val ValueColor = Color(0xFF93C5FD)
private val DividerColor = Color.White.copy(alpha = 0.08f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametersPanel(
    experimentType: ExperimentType,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    pendulumParams: PendulumParameters? = null,
    onPendulumParamsChange: ((PendulumParameters) -> Unit)? = null,
    freeFallParams: FreeFallParameters? = null,
    onFreeFallParamsChange: ((FreeFallParameters) -> Unit)? = null,
    collisionParams: CollisionParameters? = null,
    onCollisionParamsChange: ((CollisionParameters) -> Unit)? = null,
    circuitParams: CircuitParameters? = null,
    onCircuitParamsChange: ((CircuitParameters) -> Unit)? = null,
    magneticFieldParams: MagneticFieldParameters? = null,
    onMagneticFieldParamsChange: ((MagneticFieldParameters) -> Unit)? = null,
    refractionParams: RefractionParameters? = null,
    onRefractionParamsChange: ((RefractionParameters) -> Unit)? = null,
    lensParams: LensParameters? = null,
    onLensParamsChange: ((LensParameters) -> Unit)? = null,
    brownianParams: BrownianMotionParameters? = null,
    onBrownianParamsChange: ((BrownianMotionParameters) -> Unit)? = null,
    gasExpansionParams: GasExpansionParameters? = null,
    onGasExpansionParamsChange: ((GasExpansionParameters) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(PanelBg.copy(alpha = 0.97f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.22f))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(listOf(AccentViolet, AccentSky))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "Параметры",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(bottom = 8.dp))
                when (experimentType) {
                    ExperimentType.PENDULUM -> pendulumParams?.let { params ->
                        PendulumControls(params, onPendulumParamsChange ?: {})
                    }
                    ExperimentType.FREE_FALL -> freeFallParams?.let { params ->
                        FreeFallControls(params, onFreeFallParamsChange ?: {})
                    }
                    ExperimentType.COLLISION -> collisionParams?.let { params ->
                        CollisionControls(params, onCollisionParamsChange ?: {})
                    }
                    ExperimentType.ELECTRIC_CIRCUIT -> circuitParams?.let { params ->
                        CircuitControls(params, onCircuitParamsChange ?: {})
                    }
                    ExperimentType.MAGNETIC_FIELD -> magneticFieldParams?.let { params ->
                        MagneticFieldControls(params, onMagneticFieldParamsChange ?: {})
                    }
                    ExperimentType.LIGHT_REFRACTION -> refractionParams?.let { params ->
                        RefractionControls(params, onRefractionParamsChange ?: {})
                    }
                    ExperimentType.LENS -> lensParams?.let { params ->
                        LensControls(params, onLensParamsChange ?: {})
                    }
                    ExperimentType.BROWNIAN_MOTION -> brownianParams?.let { params ->
                        BrownianControls(params, onBrownianParamsChange ?: {})
                    }
                    ExperimentType.GAS_EXPANSION -> gasExpansionParams?.let { params ->
                        GasExpansionControls(params, onGasExpansionParamsChange ?: {})
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, color: Color = AccentViolet) {
    Row(
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(3.dp, 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PendulumControls(params: PendulumParameters, onParamsChange: (PendulumParameters) -> Unit) {
    ParameterSlider("Длина", params.length, { onParamsChange(params.copy(length = it)) }, 0.5f..3.0f) { "%.1f м".format(it) }
    ParameterSlider("Начальный угол", params.initialAngle, { onParamsChange(params.copy(initialAngle = it)) }, 0.1f..1.2f) { "%.0f°".format(Math.toDegrees(it.toDouble())) }
    ParameterSlider("Масса", params.mass, { onParamsChange(params.copy(mass = it)) }, 0.1f..10.0f) { "%.1f кг".format(it) }
    ParameterSlider("Затухание", params.damping, { onParamsChange(params.copy(damping = it)) }, 0f..0.1f) { "%.3f".format(it) }
}

@Composable
private fun FreeFallControls(params: FreeFallParameters, onParamsChange: (FreeFallParameters) -> Unit) {
    ParameterSlider("Начальная высота", params.initialHeight, { onParamsChange(params.copy(initialHeight = it)) }, 1f..100f) { "%.0f м".format(it) }
    ParameterSlider("Начальная скорость", params.initialVelocity, { onParamsChange(params.copy(initialVelocity = it)) }, -20f..20f) { "%.1f м/с".format(it) }
    ParameterSlider("Масса", params.mass, { onParamsChange(params.copy(mass = it)) }, 0.1f..100f) { "%.1f кг".format(it) }
    ToggleParameter("Показать след", params.showTrail) { onParamsChange(params.copy(showTrail = it)) }
}

@Composable
private fun CollisionControls(params: CollisionParameters, onParamsChange: (CollisionParameters) -> Unit) {
    SectionLabel("Шар 1", AccentViolet)
    ParameterSlider("Масса 1", params.mass1, { onParamsChange(params.copy(mass1 = it)) }, 0.1f..10f) { "%.1f кг".format(it) }
    ParameterSlider("Скорость 1", params.velocity1, { onParamsChange(params.copy(velocity1 = it)) }, -10f..10f) { "%.1f м/с".format(it) }
    SectionLabel("Шар 2", AccentSky)
    ParameterSlider("Масса 2", params.mass2, { onParamsChange(params.copy(mass2 = it)) }, 0.1f..10f) { "%.1f кг".format(it) }
    ParameterSlider("Скорость 2", params.velocity2, { onParamsChange(params.copy(velocity2 = it)) }, -10f..10f) { "%.1f м/с".format(it) }
    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))
    ToggleParameter("Упругое столкновение", params.isElastic) { onParamsChange(params.copy(isElastic = it)) }
}

@Composable
private fun CircuitControls(params: CircuitParameters, onParamsChange: (CircuitParameters) -> Unit) {
    ParameterSlider("Напряжение", params.voltage, { onParamsChange(params.copy(voltage = it)) }, 1f..24f) { "%.0f В".format(it) }
    SectionLabel("Сопротивления")
    ParameterSlider("R₁", params.resistance1, { onParamsChange(params.copy(resistance1 = it)) }, 10f..1000f) { "%.0f Ω".format(it) }
    ParameterSlider("R₂", params.resistance2, { onParamsChange(params.copy(resistance2 = it)) }, 10f..1000f) { "%.0f Ω".format(it) }
    ParameterSlider("R₃", params.resistance3, { onParamsChange(params.copy(resistance3 = it)) }, 10f..1000f) { "%.0f Ω".format(it) }
    ToggleParameter("Параллельное соединение", params.isParallel) { onParamsChange(params.copy(isParallel = it)) }
}

@Composable
private fun MagneticFieldControls(params: MagneticFieldParameters, onParamsChange: (MagneticFieldParameters) -> Unit) {
    ParameterSlider("Сила тока", params.current, { onParamsChange(params.copy(current = it)) }, 0.1f..20f) { "%.1f А".format(it) }
    ParameterSlider("Длина провода", params.wireLength, { onParamsChange(params.copy(wireLength = it)) }, 0.1f..2f) { "%.1f м".format(it) }
    ParameterSlider("Число линий поля", params.numberOfLines.toFloat(), { onParamsChange(params.copy(numberOfLines = it.roundToInt())) }, 4f..16f) { "${it.roundToInt()}" }
    ToggleParameter("Показать линии поля", params.showFieldLines) { onParamsChange(params.copy(showFieldLines = it)) }
}

@Composable
private fun RefractionControls(params: RefractionParameters, onParamsChange: (RefractionParameters) -> Unit) {
    ParameterSlider("Угол падения", params.incidentAngle, { onParamsChange(params.copy(incidentAngle = it)) }, 0f..1.5f) { "%.0f°".format(Math.toDegrees(it.toDouble())) }
    ParameterSlider("n₁ (среда 1)", params.n1, { onParamsChange(params.copy(n1 = it)) }, 1.0f..2.5f) { "%.2f".format(it) }
    ParameterSlider("n₂ (среда 2)", params.n2, { onParamsChange(params.copy(n2 = it)) }, 1.0f..2.5f) { "%.2f".format(it) }
    ToggleParameter("Показать нормаль", params.showNormal) { onParamsChange(params.copy(showNormal = it)) }
    SectionLabel("Быстрый выбор материала")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MaterialChip("Воздух", params.n1 == 1.0f) { onParamsChange(params.copy(n1 = 1.0f)) }
        MaterialChip("Вода", params.n2 == 1.33f) { onParamsChange(params.copy(n2 = 1.33f)) }
        MaterialChip("Стекло", params.n2 == 1.5f) { onParamsChange(params.copy(n2 = 1.5f)) }
        MaterialChip("Алмаз", params.n2 == 2.42f) { onParamsChange(params.copy(n2 = 2.42f)) }
    }
}

@Composable
private fun LensControls(params: LensParameters, onParamsChange: (LensParameters) -> Unit) {
    ParameterSlider("Фокусное расстояние", params.focalLength, { onParamsChange(params.copy(focalLength = it)) }, 0.1f..2.0f) { "%.2f м".format(it) }
    ParameterSlider("Расстояние до объекта", params.objectDistance, { onParamsChange(params.copy(objectDistance = it)) }, 0.2f..5.0f) { "%.2f м".format(it) }
    ParameterSlider("Высота объекта", params.objectHeight, { onParamsChange(params.copy(objectHeight = it)) }, 0.1f..1.0f) { "%.2f м".format(it) }
    ToggleParameter("Собирающая линза", params.isConverging) { onParamsChange(params.copy(isConverging = it)) }
}

@Composable
private fun BrownianControls(params: BrownianMotionParameters, onParamsChange: (BrownianMotionParameters) -> Unit) {
    ParameterSlider("Температура", params.temperature, { onParamsChange(params.copy(temperature = it)) }, 200f..500f) { "%.0f K (%.0f°C)".format(it, it - 273f) }
    ParameterSlider("Вязкость среды", params.viscosity * 1000f, { onParamsChange(params.copy(viscosity = it / 1000f)) }, 0.5f..5f) { "%.2f мПа·с".format(it) }
    ParameterSlider("Число частиц", params.numberOfParticles.toFloat(), { onParamsChange(params.copy(numberOfParticles = it.roundToInt())) }, 5f..50f) { "${it.roundToInt()}" }
}

@Composable
private fun GasExpansionControls(params: GasExpansionParameters, onParamsChange: (GasExpansionParameters) -> Unit) {
    ParameterSlider("Начальная температура", params.initialTemperature, { onParamsChange(params.copy(initialTemperature = it)) }, 200f..400f) { "%.0f K".format(it) }
    ParameterSlider("Конечная температура", params.finalTemperature, { onParamsChange(params.copy(finalTemperature = it)) }, 300f..600f) { "%.0f K".format(it) }
    ParameterSlider("Количество вещества", params.moles, { onParamsChange(params.copy(moles = it)) }, 0.1f..5f) { "%.1f моль".format(it) }
}

@Composable
private fun ParameterSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    formatValue: (Float) -> String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = LabelColor,
                fontSize = 12.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentViolet.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = formatValue(value),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = ValueColor,
                    fontSize = 11.sp
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth().height(32.dp),
            colors = SliderDefaults.colors(
                thumbColor = AccentViolet,
                activeTrackColor = AccentViolet,
                inactiveTrackColor = Color.White.copy(alpha = 0.14f),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ToggleParameter(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (checked) Color.White else LabelColor,
            fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentViolet,
                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
                uncheckedBorderColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun MaterialChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) AccentViolet.copy(alpha = 0.85f)
                else Color.White.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (selected) Color.White else LabelColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
