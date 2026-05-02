package com.app.dopp.physics

import androidx.compose.runtime.Stable

/**
 * Experiment categories
 */
enum class ExperimentCategory(val displayName: String, val description: String) {
    MECHANICS("Механика", "Движение тел, силы, энергия"),
    ELECTRICITY("Электричество", "Цепи, ток, магнетизм"),
    OPTICS("Оптика", "Свет, преломление, линзы"),
    THERMODYNAMICS("Термодинамика", "Тепло, газы, молекулы")
}

/**
 * Experiment types
 */
enum class ExperimentType(
    val displayName: String,
    val description: String,
    val category: ExperimentCategory
) {
    // Mechanics
    PENDULUM(
        "Маятник",
        "Колебания простого маятника с настраиваемой длиной и начальным углом",
        ExperimentCategory.MECHANICS
    ),
    FREE_FALL(
        "Свободное падение",
        "Падение тел под действием гравитации с разной начальной высотой",
        ExperimentCategory.MECHANICS
    ),
    COLLISION(
        "Столкновение шаров",
        "Упругое и неупругое столкновение двух тел",
        ExperimentCategory.MECHANICS
    ),
    
    // Electricity
    ELECTRIC_CIRCUIT(
        "Электрическая цепь",
        "Последовательное и параллельное соединение резисторов",
        ExperimentCategory.ELECTRICITY
    ),
    MAGNETIC_FIELD(
        "Магнитное поле",
        "Визуализация магнитного поля проводника с током",
        ExperimentCategory.ELECTRICITY
    ),
    
    // Optics
    LIGHT_REFRACTION(
        "Преломление света",
        "Преломление света на границе двух сред",
        ExperimentCategory.OPTICS
    ),
    LENS(
        "Линзы",
        "Фокусировка света собирающей и рассеивающей линзами",
        ExperimentCategory.OPTICS
    ),
    
    // Thermodynamics
    BROWNIAN_MOTION(
        "Броуновское движение",
        "Хаотическое движение частиц в жидкости",
        ExperimentCategory.THERMODYNAMICS
    ),
    GAS_EXPANSION(
        "Расширение газа",
        "Изменение объёма газа при нагревании",
        ExperimentCategory.THERMODYNAMICS
    )
}

/**
 * Base interface for experiment parameters
 */
interface ExperimentParameters

/**
 * Base interface for experiment state
 */
interface ExperimentState {
    val isRunning: Boolean
    val time: Float
}

// ==================== MECHANICS EXPERIMENTS ====================

@Stable
data class PendulumParameters(
    val length: Float = 1.0f, // meters (0.5 - 3.0)
    val initialAngle: Float = 0.5f, // radians (0.1 - 1.2)
    val mass: Float = 1.0f, // kg (0.1 - 10.0)
    val damping: Float = 0.02f // damping coefficient (0 - 0.1)
) : ExperimentParameters

@Stable
data class PendulumState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val currentAngle: Float = 0f,
    val angularVelocity: Float = 0f,
    val period: Float = 0f,
    val kineticEnergy: Float = 0f,
    val potentialEnergy: Float = 0f
) : ExperimentState

@Stable
data class FreeFallParameters(
    val initialHeight: Float = 10f, // meters (1 - 100)
    val initialVelocity: Float = 0f, // m/s (-20 - 20)
    val mass: Float = 1.0f, // kg (0.1 - 100)
    val showTrail: Boolean = true
) : ExperimentParameters

@Stable
data class FreeFallState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val currentHeight: Float = 0f,
    val currentVelocity: Float = 0f,
    val hasLanded: Boolean = false,
    val maxVelocity: Float = 0f
) : ExperimentState

@Stable
data class CollisionParameters(
    val mass1: Float = 2.0f, // kg (0.1 - 10)
    val mass2: Float = 1.0f, // kg (0.1 - 10)
    val velocity1: Float = 5.0f, // m/s (-10 - 10)
    val velocity2: Float = -3.0f, // m/s (-10 - 10)
    val isElastic: Boolean = true
) : ExperimentParameters

@Stable
data class CollisionState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val position1: Float = -2f,
    val position2: Float = 2f,
    val currentVelocity1: Float = 0f,
    val currentVelocity2: Float = 0f,
    val hasCollided: Boolean = false,
    val totalMomentumBefore: Float = 0f,
    val totalMomentumAfter: Float = 0f,
    val totalEnergyBefore: Float = 0f,
    val totalEnergyAfter: Float = 0f
) : ExperimentState

// ==================== ELECTRICITY EXPERIMENTS ====================

@Stable
data class CircuitParameters(
    val voltage: Float = 12f, // Volts (1 - 24)
    val resistance1: Float = 100f, // Ohms (10 - 1000)
    val resistance2: Float = 200f, // Ohms (10 - 1000)
    val resistance3: Float = 150f, // Ohms (10 - 1000)
    val isParallel: Boolean = false
) : ExperimentParameters

@Stable
data class CircuitState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val totalResistance: Float = 0f,
    val totalCurrent: Float = 0f,
    val current1: Float = 0f,
    val current2: Float = 0f,
    val current3: Float = 0f,
    val voltage1: Float = 0f,
    val voltage2: Float = 0f,
    val voltage3: Float = 0f,
    val power: Float = 0f,
    val electronFlowPhase: Float = 0f
) : ExperimentState

@Stable
data class MagneticFieldParameters(
    val current: Float = 5f, // Amperes (0.1 - 20)
    val wireLength: Float = 1f, // meters (0.1 - 2)
    val showFieldLines: Boolean = true,
    val numberOfLines: Int = 8
) : ExperimentParameters

@Stable
data class MagneticFieldState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val fieldStrengthAtDistance: Map<Float, Float> = emptyMap(),
    val animationPhase: Float = 0f
) : ExperimentState

// ==================== OPTICS EXPERIMENTS ====================

@Stable
data class RefractionParameters(
    val incidentAngle: Float = 0.5f, // radians (0 - π/2)
    val n1: Float = 1.0f, // refractive index medium 1 (air = 1.0)
    val n2: Float = 1.5f, // refractive index medium 2 (glass ≈ 1.5, water ≈ 1.33)
    val showNormal: Boolean = true
) : ExperimentParameters

@Stable
data class RefractionState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val refractedAngle: Float? = null,
    val isTotalInternalReflection: Boolean = false,
    val criticalAngle: Float? = null,
    val rayAnimationProgress: Float = 0f
) : ExperimentState

@Stable
data class LensParameters(
    val focalLength: Float = 0.5f, // meters (0.1 - 2.0)
    val objectDistance: Float = 1.0f, // meters (0.2 - 5.0)
    val objectHeight: Float = 0.3f, // meters (0.1 - 1.0)
    val isConverging: Boolean = true
) : ExperimentParameters

@Stable
data class LensState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val imageDistance: Float = 0f,
    val imageHeight: Float = 0f,
    val magnification: Float = 0f,
    val isVirtual: Boolean = false,
    val isInverted: Boolean = false,
    val rayAnimationProgress: Float = 0f
) : ExperimentState

// ==================== THERMODYNAMICS EXPERIMENTS ====================

@Stable
data class BrownianMotionParameters(
    val temperature: Float = 300f, // Kelvin (200 - 500)
    val particleRadius: Float = 1e-6f, // meters (micrometers)
    val viscosity: Float = 0.001f, // Pa·s (water ≈ 0.001)
    val numberOfParticles: Int = 20
) : ExperimentParameters

@Stable
data class BrownianMotionState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val particlePositions: List<Pair<Float, Float>> = emptyList(),
    val particleTrails: List<List<Pair<Float, Float>>> = emptyList()
) : ExperimentState

@Stable
data class GasExpansionParameters(
    val initialTemperature: Float = 300f, // Kelvin (200 - 600)
    val finalTemperature: Float = 450f, // Kelvin (200 - 600)
    val initialVolume: Float = 1f, // arbitrary units
    val moles: Float = 1f, // moles of gas
    val pressure: Float = 101325f // Pascals (atmospheric)
) : ExperimentParameters

@Stable
data class GasExpansionState(
    override val isRunning: Boolean = false,
    override val time: Float = 0f,
    val currentTemperature: Float = 0f,
    val currentVolume: Float = 0f,
    val currentPressure: Float = 0f,
    val moleculePositions: List<Triple<Float, Float, Float>> = emptyList(),
    val moleculeSpeeds: List<Float> = emptyList()
) : ExperimentState
