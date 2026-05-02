package com.app.dopp.ui_project.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dopp.physics.*
import kotlin.math.roundToInt

/**
 * Sliding parameters panel for experiment controls
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametersPanel(
    experimentType: ExperimentType,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    // Mechanics
    pendulumParams: PendulumParameters? = null,
    onPendulumParamsChange: ((PendulumParameters) -> Unit)? = null,
    freeFallParams: FreeFallParameters? = null,
    onFreeFallParamsChange: ((FreeFallParameters) -> Unit)? = null,
    collisionParams: CollisionParameters? = null,
    onCollisionParamsChange: ((CollisionParameters) -> Unit)? = null,
    // Electricity
    circuitParams: CircuitParameters? = null,
    onCircuitParamsChange: ((CircuitParameters) -> Unit)? = null,
    magneticFieldParams: MagneticFieldParameters? = null,
    onMagneticFieldParamsChange: ((MagneticFieldParameters) -> Unit)? = null,
    // Optics
    refractionParams: RefractionParameters? = null,
    onRefractionParamsChange: ((RefractionParameters) -> Unit)? = null,
    lensParams: LensParameters? = null,
    onLensParamsChange: ((LensParameters) -> Unit)? = null,
    // Thermodynamics
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
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp),
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                onClick = onToggle
            ) {}
        }
        
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Параметры",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
                )
            }
        }
        
        // Content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
private fun PendulumControls(
    params: PendulumParameters,
    onParamsChange: (PendulumParameters) -> Unit
) {
    ParameterSlider(
        label = "Длина (м)",
        value = params.length,
        onValueChange = { onParamsChange(params.copy(length = it)) },
        valueRange = 0.5f..3.0f,
        formatValue = { "%.1f м".format(it) }
    )
    
    ParameterSlider(
        label = "Начальный угол",
        value = params.initialAngle,
        onValueChange = { onParamsChange(params.copy(initialAngle = it)) },
        valueRange = 0.1f..1.2f,
        formatValue = { "%.0f°".format(Math.toDegrees(it.toDouble())) }
    )
    
    ParameterSlider(
        label = "Масса (кг)",
        value = params.mass,
        onValueChange = { onParamsChange(params.copy(mass = it)) },
        valueRange = 0.1f..10.0f,
        formatValue = { "%.1f кг".format(it) }
    )
    
    ParameterSlider(
        label = "Затухание",
        value = params.damping,
        onValueChange = { onParamsChange(params.copy(damping = it)) },
        valueRange = 0f..0.1f,
        formatValue = { "%.3f".format(it) }
    )
}

@Composable
private fun FreeFallControls(
    params: FreeFallParameters,
    onParamsChange: (FreeFallParameters) -> Unit
) {
    ParameterSlider(
        label = "Начальная высота (м)",
        value = params.initialHeight,
        onValueChange = { onParamsChange(params.copy(initialHeight = it)) },
        valueRange = 1f..100f,
        formatValue = { "%.0f м".format(it) }
    )
    
    ParameterSlider(
        label = "Начальная скорость (м/с)",
        value = params.initialVelocity,
        onValueChange = { onParamsChange(params.copy(initialVelocity = it)) },
        valueRange = -20f..20f,
        formatValue = { "%.1f м/с".format(it) }
    )
    
    ParameterSlider(
        label = "Масса (кг)",
        value = params.mass,
        onValueChange = { onParamsChange(params.copy(mass = it)) },
        valueRange = 0.1f..100f,
        formatValue = { "%.1f кг".format(it) }
    )
    
    ToggleParameter(
        label = "Показать след",
        checked = params.showTrail,
        onCheckedChange = { onParamsChange(params.copy(showTrail = it)) }
    )
}

@Composable
private fun CollisionControls(
    params: CollisionParameters,
    onParamsChange: (CollisionParameters) -> Unit
) {
    Text(
        text = "Шар 1",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
    
    ParameterSlider(
        label = "Масса 1 (кг)",
        value = params.mass1,
        onValueChange = { onParamsChange(params.copy(mass1 = it)) },
        valueRange = 0.1f..10f,
        formatValue = { "%.1f кг".format(it) }
    )
    
    ParameterSlider(
        label = "Скорость 1 (м/с)",
        value = params.velocity1,
        onValueChange = { onParamsChange(params.copy(velocity1 = it)) },
        valueRange = -10f..10f,
        formatValue = { "%.1f м/с".format(it) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
        text = "Шар 2",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )
    
    ParameterSlider(
        label = "Масса 2 (кг)",
        value = params.mass2,
        onValueChange = { onParamsChange(params.copy(mass2 = it)) },
        valueRange = 0.1f..10f,
        formatValue = { "%.1f кг".format(it) }
    )
    
    ParameterSlider(
        label = "Скорость 2 (м/с)",
        value = params.velocity2,
        onValueChange = { onParamsChange(params.copy(velocity2 = it)) },
        valueRange = -10f..10f,
        formatValue = { "%.1f м/с".format(it) }
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    ToggleParameter(
        label = "Упругое столкновение",
        checked = params.isElastic,
        onCheckedChange = { onParamsChange(params.copy(isElastic = it)) }
    )
}

@Composable
private fun CircuitControls(
    params: CircuitParameters,
    onParamsChange: (CircuitParameters) -> Unit
) {
    ParameterSlider(
        label = "Напряжение (В)",
        value = params.voltage,
        onValueChange = { onParamsChange(params.copy(voltage = it)) },
        valueRange = 1f..24f,
        formatValue = { "%.0f В".format(it) }
    )
    
    ParameterSlider(
        label = "R₁ (Ом)",
        value = params.resistance1,
        onValueChange = { onParamsChange(params.copy(resistance1 = it)) },
        valueRange = 10f..1000f,
        formatValue = { "%.0f Ω".format(it) }
    )
    
    ParameterSlider(
        label = "R₂ (Ом)",
        value = params.resistance2,
        onValueChange = { onParamsChange(params.copy(resistance2 = it)) },
        valueRange = 10f..1000f,
        formatValue = { "%.0f Ω".format(it) }
    )
    
    ParameterSlider(
        label = "R₃ (Ом)",
        value = params.resistance3,
        onValueChange = { onParamsChange(params.copy(resistance3 = it)) },
        valueRange = 10f..1000f,
        formatValue = { "%.0f Ω".format(it) }
    )
    
    ToggleParameter(
        label = "Параллельное соединение",
        checked = params.isParallel,
        onCheckedChange = { onParamsChange(params.copy(isParallel = it)) }
    )
}

@Composable
private fun MagneticFieldControls(
    params: MagneticFieldParameters,
    onParamsChange: (MagneticFieldParameters) -> Unit
) {
    ParameterSlider(
        label = "Сила тока (А)",
        value = params.current,
        onValueChange = { onParamsChange(params.copy(current = it)) },
        valueRange = 0.1f..20f,
        formatValue = { "%.1f А".format(it) }
    )
    
    ParameterSlider(
        label = "Длина провода (м)",
        value = params.wireLength,
        onValueChange = { onParamsChange(params.copy(wireLength = it)) },
        valueRange = 0.1f..2f,
        formatValue = { "%.1f м".format(it) }
    )
    
    ParameterSlider(
        label = "Число линий поля",
        value = params.numberOfLines.toFloat(),
        onValueChange = { onParamsChange(params.copy(numberOfLines = it.roundToInt())) },
        valueRange = 4f..16f,
        formatValue = { "${it.roundToInt()}" }
    )
    
    ToggleParameter(
        label = "Показать линии поля",
        checked = params.showFieldLines,
        onCheckedChange = { onParamsChange(params.copy(showFieldLines = it)) }
    )
}

@Composable
private fun RefractionControls(
    params: RefractionParameters,
    onParamsChange: (RefractionParameters) -> Unit
) {
    ParameterSlider(
        label = "Угол падения",
        value = params.incidentAngle,
        onValueChange = { onParamsChange(params.copy(incidentAngle = it)) },
        valueRange = 0f..1.5f,
        formatValue = { "%.0f°".format(Math.toDegrees(it.toDouble())) }
    )
    
    ParameterSlider(
        label = "n₁ (среда 1)",
        value = params.n1,
        onValueChange = { onParamsChange(params.copy(n1 = it)) },
        valueRange = 1.0f..2.5f,
        formatValue = { "%.2f".format(it) }
    )
    
    ParameterSlider(
        label = "n₂ (среда 2)",
        value = params.n2,
        onValueChange = { onParamsChange(params.copy(n2 = it)) },
        valueRange = 1.0f..2.5f,
        formatValue = { "%.2f".format(it) }
    )
    
    ToggleParameter(
        label = "Показать нормаль",
        checked = params.showNormal,
        onCheckedChange = { onParamsChange(params.copy(showNormal = it)) }
    )
    
    // Preset materials
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MaterialChip(
            label = "Воздух",
            selected = params.n1 == 1.0f,
            onClick = { onParamsChange(params.copy(n1 = 1.0f)) }
        )
        MaterialChip(
            label = "Вода",
            selected = params.n2 == 1.33f,
            onClick = { onParamsChange(params.copy(n2 = 1.33f)) }
        )
        MaterialChip(
            label = "Стекло",
            selected = params.n2 == 1.5f,
            onClick = { onParamsChange(params.copy(n2 = 1.5f)) }
        )
        MaterialChip(
            label = "Алмаз",
            selected = params.n2 == 2.42f,
            onClick = { onParamsChange(params.copy(n2 = 2.42f)) }
        )
    }
}

@Composable
private fun LensControls(
    params: LensParameters,
    onParamsChange: (LensParameters) -> Unit
) {
    ParameterSlider(
        label = "Фокусное расстояние (м)",
        value = params.focalLength,
        onValueChange = { onParamsChange(params.copy(focalLength = it)) },
        valueRange = 0.1f..2.0f,
        formatValue = { "%.2f м".format(it) }
    )
    
    ParameterSlider(
        label = "Расстояние до объекта (м)",
        value = params.objectDistance,
        onValueChange = { onParamsChange(params.copy(objectDistance = it)) },
        valueRange = 0.2f..5.0f,
        formatValue = { "%.2f м".format(it) }
    )
    
    ParameterSlider(
        label = "Высота объекта (м)",
        value = params.objectHeight,
        onValueChange = { onParamsChange(params.copy(objectHeight = it)) },
        valueRange = 0.1f..1.0f,
        formatValue = { "%.2f м".format(it) }
    )
    
    ToggleParameter(
        label = "Собирающая линза",
        checked = params.isConverging,
        onCheckedChange = { onParamsChange(params.copy(isConverging = it)) }
    )
}

@Composable
private fun BrownianControls(
    params: BrownianMotionParameters,
    onParamsChange: (BrownianMotionParameters) -> Unit
) {
    ParameterSlider(
        label = "Температура (K)",
        value = params.temperature,
        onValueChange = { onParamsChange(params.copy(temperature = it)) },
        valueRange = 200f..500f,
        formatValue = { "%.0f K (%.0f°C)".format(it, it - 273f) }
    )
    
    ParameterSlider(
        label = "Вязкость среды",
        value = params.viscosity * 1000f,
        onValueChange = { onParamsChange(params.copy(viscosity = it / 1000f)) },
        valueRange = 0.5f..5f,
        formatValue = { "%.2f мПа·с".format(it) }
    )
    
    ParameterSlider(
        label = "Число частиц",
        value = params.numberOfParticles.toFloat(),
        onValueChange = { onParamsChange(params.copy(numberOfParticles = it.roundToInt())) },
        valueRange = 5f..50f,
        formatValue = { "${it.roundToInt()}" }
    )
}

@Composable
private fun GasExpansionControls(
    params: GasExpansionParameters,
    onParamsChange: (GasExpansionParameters) -> Unit
) {
    ParameterSlider(
        label = "Начальная температура (K)",
        value = params.initialTemperature,
        onValueChange = { onParamsChange(params.copy(initialTemperature = it)) },
        valueRange = 200f..400f,
        formatValue = { "%.0f K".format(it) }
    )
    
    ParameterSlider(
        label = "Конечная температура (K)",
        value = params.finalTemperature,
        onValueChange = { onParamsChange(params.copy(finalTemperature = it)) },
        valueRange = 300f..600f,
        formatValue = { "%.0f K".format(it) }
    )
    
    ParameterSlider(
        label = "Количество вещества (моль)",
        value = params.moles,
        onValueChange = { onParamsChange(params.copy(moles = it)) },
        valueRange = 0.1f..5f,
        formatValue = { "%.1f моль".format(it) }
    )
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
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatValue(value),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
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
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MaterialChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
    )
}
