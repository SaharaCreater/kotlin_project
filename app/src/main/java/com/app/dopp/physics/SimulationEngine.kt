package com.app.dopp.physics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*
import kotlin.random.Random

/**
 * Simulation engine that runs physics calculations for all experiments
 */
class SimulationEngine {
    
    private val deltaTime = 0.016f // ~60 FPS
    
    // ==================== PENDULUM ====================
    
    fun updatePendulum(
        params: PendulumParameters,
        currentState: PendulumState
    ): PendulumState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        val angle = PhysicsCalculations.pendulumAngle(
            params.initialAngle,
            params.length,
            newTime,
            params.damping
        )
        val angularVelocity = PhysicsCalculations.pendulumAngularVelocity(
            params.initialAngle,
            params.length,
            newTime,
            params.damping
        )
        val period = PhysicsCalculations.pendulumPeriod(params.length)
        
        // Calculate energies
        val height = params.length * (1 - cos(angle))
        val potentialEnergy = params.mass * PhysicsCalculations.GRAVITY * height
        val velocity = params.length * abs(angularVelocity)
        val kineticEnergy = PhysicsCalculations.kineticEnergy(params.mass, velocity)
        
        return currentState.copy(
            time = newTime,
            currentAngle = angle,
            angularVelocity = angularVelocity,
            period = period,
            kineticEnergy = kineticEnergy,
            potentialEnergy = potentialEnergy
        )
    }
    
    fun initPendulum(params: PendulumParameters): PendulumState {
        return PendulumState(
            isRunning = true,
            time = 0f,
            currentAngle = params.initialAngle,
            angularVelocity = 0f,
            period = PhysicsCalculations.pendulumPeriod(params.length)
        )
    }
    
    // ==================== FREE FALL ====================
    
    fun updateFreeFall(
        params: FreeFallParameters,
        currentState: FreeFallState
    ): FreeFallState {
        if (!currentState.isRunning || currentState.hasLanded) return currentState
        
        val newTime = currentState.time + deltaTime
        val newHeight = PhysicsCalculations.freeFallPosition(
            params.initialHeight,
            params.initialVelocity,
            newTime
        )
        val newVelocity = PhysicsCalculations.freeFallVelocity(
            params.initialVelocity,
            newTime
        )
        
        val hasLanded = newHeight <= 0f
        val maxVelocity = maxOf(currentState.maxVelocity, abs(newVelocity))
        
        return currentState.copy(
            time = newTime,
            currentHeight = maxOf(0f, newHeight),
            currentVelocity = if (hasLanded) 0f else newVelocity,
            hasLanded = hasLanded,
            maxVelocity = maxVelocity
        )
    }
    
    fun initFreeFall(params: FreeFallParameters): FreeFallState {
        return FreeFallState(
            isRunning = true,
            time = 0f,
            currentHeight = params.initialHeight,
            currentVelocity = params.initialVelocity,
            hasLanded = false,
            maxVelocity = abs(params.initialVelocity)
        )
    }
    
    // ==================== COLLISION ====================
    
    fun updateCollision(
        params: CollisionParameters,
        currentState: CollisionState
    ): CollisionState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        var newPos1 = currentState.position1 + currentState.currentVelocity1 * deltaTime
        var newPos2 = currentState.position2 + currentState.currentVelocity2 * deltaTime
        var newVel1 = currentState.currentVelocity1
        var newVel2 = currentState.currentVelocity2
        var hasCollided = currentState.hasCollided
        var momentumAfter = currentState.totalMomentumAfter
        var energyAfter = currentState.totalEnergyAfter
        
        // Check for collision (balls have radius 0.3)
        val ballRadius = 0.3f
        if (!hasCollided && abs(newPos1 - newPos2) <= 2 * ballRadius) {
            hasCollided = true
            if (params.isElastic) {
                val (v1, v2) = PhysicsCalculations.elasticCollision(
                    params.mass1, currentState.currentVelocity1,
                    params.mass2, currentState.currentVelocity2
                )
                newVel1 = v1
                newVel2 = v2
            } else {
                val v = PhysicsCalculations.inelasticCollision(
                    params.mass1, currentState.currentVelocity1,
                    params.mass2, currentState.currentVelocity2
                )
                newVel1 = v
                newVel2 = v
            }
            momentumAfter = PhysicsCalculations.momentum(params.mass1, newVel1) +
                    PhysicsCalculations.momentum(params.mass2, newVel2)
            energyAfter = PhysicsCalculations.kineticEnergy(params.mass1, newVel1) +
                    PhysicsCalculations.kineticEnergy(params.mass2, newVel2)
        }
        
        return currentState.copy(
            time = newTime,
            position1 = newPos1,
            position2 = newPos2,
            currentVelocity1 = newVel1,
            currentVelocity2 = newVel2,
            hasCollided = hasCollided,
            totalMomentumAfter = momentumAfter,
            totalEnergyAfter = energyAfter
        )
    }
    
    fun initCollision(params: CollisionParameters): CollisionState {
        val momentumBefore = PhysicsCalculations.momentum(params.mass1, params.velocity1) +
                PhysicsCalculations.momentum(params.mass2, params.velocity2)
        val energyBefore = PhysicsCalculations.kineticEnergy(params.mass1, params.velocity1) +
                PhysicsCalculations.kineticEnergy(params.mass2, params.velocity2)
        
        return CollisionState(
            isRunning = true,
            time = 0f,
            position1 = -2f,
            position2 = 2f,
            currentVelocity1 = params.velocity1,
            currentVelocity2 = params.velocity2,
            hasCollided = false,
            totalMomentumBefore = momentumBefore,
            totalMomentumAfter = momentumBefore,
            totalEnergyBefore = energyBefore,
            totalEnergyAfter = energyBefore
        )
    }
    
    // ==================== ELECTRIC CIRCUIT ====================
    
    fun updateCircuit(
        params: CircuitParameters,
        currentState: CircuitState
    ): CircuitState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        val newPhase = (currentState.electronFlowPhase + deltaTime * 2f) % (2f * PI.toFloat())
        
        val totalResistance = if (params.isParallel) {
            PhysicsCalculations.parallelResistance(
                params.resistance1,
                params.resistance2,
                params.resistance3
            )
        } else {
            PhysicsCalculations.seriesResistance(
                params.resistance1,
                params.resistance2,
                params.resistance3
            )
        }
        
        val totalCurrent = PhysicsCalculations.current(params.voltage, totalResistance)
        
        val (current1, current2, current3) = if (params.isParallel) {
            Triple(
                PhysicsCalculations.current(params.voltage, params.resistance1),
                PhysicsCalculations.current(params.voltage, params.resistance2),
                PhysicsCalculations.current(params.voltage, params.resistance3)
            )
        } else {
            Triple(totalCurrent, totalCurrent, totalCurrent)
        }
        
        val (voltage1, voltage2, voltage3) = if (params.isParallel) {
            Triple(params.voltage, params.voltage, params.voltage)
        } else {
            Triple(
                PhysicsCalculations.voltage(totalCurrent, params.resistance1),
                PhysicsCalculations.voltage(totalCurrent, params.resistance2),
                PhysicsCalculations.voltage(totalCurrent, params.resistance3)
            )
        }
        
        val power = PhysicsCalculations.electricPower(params.voltage, totalCurrent)
        
        return currentState.copy(
            time = newTime,
            totalResistance = totalResistance,
            totalCurrent = totalCurrent,
            current1 = current1,
            current2 = current2,
            current3 = current3,
            voltage1 = voltage1,
            voltage2 = voltage2,
            voltage3 = voltage3,
            power = power,
            electronFlowPhase = newPhase
        )
    }
    
    fun initCircuit(params: CircuitParameters): CircuitState {
        return CircuitState(isRunning = true, time = 0f)
    }
    
    // ==================== MAGNETIC FIELD ====================
    
    fun updateMagneticField(
        params: MagneticFieldParameters,
        currentState: MagneticFieldState
    ): MagneticFieldState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        val newPhase = (currentState.animationPhase + deltaTime * params.current * 0.5f) % (2f * PI.toFloat())
        
        val distances = listOf(0.01f, 0.02f, 0.05f, 0.1f, 0.2f, 0.5f)
        val fieldStrengths = distances.associateWith { distance ->
            PhysicsCalculations.magneticFieldWire(params.current, distance)
        }
        
        return currentState.copy(
            time = newTime,
            fieldStrengthAtDistance = fieldStrengths,
            animationPhase = newPhase
        )
    }
    
    fun initMagneticField(params: MagneticFieldParameters): MagneticFieldState {
        return MagneticFieldState(isRunning = true, time = 0f)
    }
    
    // ==================== REFRACTION ====================
    
    fun updateRefraction(
        params: RefractionParameters,
        currentState: RefractionState
    ): RefractionState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        val newProgress = minOf(1f, currentState.rayAnimationProgress + deltaTime * 0.5f)
        
        val refractedAngle = PhysicsCalculations.refractionAngle(
            params.incidentAngle,
            params.n1,
            params.n2
        )
        
        val criticalAngle = PhysicsCalculations.criticalAngle(params.n1, params.n2)
        val isTIR = refractedAngle == null
        
        return currentState.copy(
            time = newTime,
            refractedAngle = refractedAngle,
            isTotalInternalReflection = isTIR,
            criticalAngle = criticalAngle,
            rayAnimationProgress = newProgress
        )
    }
    
    fun initRefraction(params: RefractionParameters): RefractionState {
        return RefractionState(isRunning = true, time = 0f, rayAnimationProgress = 0f)
    }
    
    // ==================== LENS ====================
    
    fun updateLens(
        params: LensParameters,
        currentState: LensState
    ): LensState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        val newProgress = minOf(1f, currentState.rayAnimationProgress + deltaTime * 0.3f)
        
        val effectiveFocalLength = if (params.isConverging) params.focalLength else -params.focalLength
        val imageDistance = PhysicsCalculations.imageDistance(params.objectDistance, effectiveFocalLength)
        val magnification = PhysicsCalculations.magnification(params.objectDistance, imageDistance)
        val imageHeight = params.objectHeight * magnification
        
        val isVirtual = imageDistance < 0
        val isInverted = magnification < 0
        
        return currentState.copy(
            time = newTime,
            imageDistance = imageDistance,
            imageHeight = abs(imageHeight),
            magnification = magnification,
            isVirtual = isVirtual,
            isInverted = isInverted,
            rayAnimationProgress = newProgress
        )
    }
    
    fun initLens(params: LensParameters): LensState {
        return LensState(isRunning = true, time = 0f, rayAnimationProgress = 0f)
    }
    
    // ==================== BROWNIAN MOTION ====================
    
    fun updateBrownianMotion(
        params: BrownianMotionParameters,
        currentState: BrownianMotionState
    ): BrownianMotionState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        
        // Scale factor for visualization (actual Brownian motion is too small to see)
        val scaleFactor = 1e5f
        
        val newPositions = currentState.particlePositions.map { (x, y) ->
            val (dx, dy) = PhysicsCalculations.brownianStep(
                params.temperature,
                params.viscosity,
                params.particleRadius,
                deltaTime
            )
            val newX = (x + dx * scaleFactor).coerceIn(-1f, 1f)
            val newY = (y + dy * scaleFactor).coerceIn(-1f, 1f)
            Pair(newX, newY)
        }
        
        // Update trails (keep last 50 positions)
        val newTrails = currentState.particleTrails.mapIndexed { index, trail ->
            val currentPos = newPositions.getOrNull(index) ?: Pair(0f, 0f)
            (trail + currentPos).takeLast(50)
        }
        
        return currentState.copy(
            time = newTime,
            particlePositions = newPositions,
            particleTrails = newTrails
        )
    }
    
    fun initBrownianMotion(params: BrownianMotionParameters): BrownianMotionState {
        val positions = (0 until params.numberOfParticles).map {
            Pair(Random.nextFloat() * 2f - 1f, Random.nextFloat() * 2f - 1f)
        }
        val trails = positions.map { listOf(it) }
        
        return BrownianMotionState(
            isRunning = true,
            time = 0f,
            particlePositions = positions,
            particleTrails = trails
        )
    }
    
    // ==================== GAS EXPANSION ====================
    
    fun updateGasExpansion(
        params: GasExpansionParameters,
        currentState: GasExpansionState
    ): GasExpansionState {
        if (!currentState.isRunning) return currentState
        
        val newTime = currentState.time + deltaTime
        
        // Gradually change temperature
        val progress = minOf(1f, newTime / 5f) // 5 seconds to reach final temperature
        val currentTemp = params.initialTemperature + 
            (params.finalTemperature - params.initialTemperature) * progress
        
        // Calculate volume using ideal gas law (isobaric process)
        val currentVolume = params.initialVolume * (currentTemp / params.initialTemperature)
        
        // Calculate pressure (should stay constant in isobaric)
        val currentPressure = params.pressure
        
        // Update molecule positions with temperature-dependent speed
        val speed = PhysicsCalculations.rmsSpeed(currentTemp, 4.65e-26f) // Nitrogen molecule mass
        val speedScale = speed / 500f // Scale for visualization
        
        val newMoleculePositions = currentState.moleculePositions.map { (x, y, z) ->
            val dx = (Random.nextFloat() - 0.5f) * speedScale * deltaTime
            val dy = (Random.nextFloat() - 0.5f) * speedScale * deltaTime
            val dz = (Random.nextFloat() - 0.5f) * speedScale * deltaTime
            
            // Keep within expanding container
            val containerSize = sqrt(currentVolume / params.initialVolume).toFloat()
            Triple(
                (x + dx).coerceIn(-containerSize, containerSize),
                (y + dy).coerceIn(-containerSize, containerSize),
                (z + dz).coerceIn(-containerSize, containerSize)
            )
        }
        
        return currentState.copy(
            time = newTime,
            currentTemperature = currentTemp,
            currentVolume = currentVolume,
            currentPressure = currentPressure,
            moleculePositions = newMoleculePositions,
            moleculeSpeeds = newMoleculePositions.map { speed }
        )
    }
    
    fun initGasExpansion(params: GasExpansionParameters): GasExpansionState {
        val numMolecules = 30
        val positions = (0 until numMolecules).map {
            Triple(
                Random.nextFloat() * 2f - 1f,
                Random.nextFloat() * 2f - 1f,
                Random.nextFloat() * 2f - 1f
            )
        }
        
        return GasExpansionState(
            isRunning = true,
            time = 0f,
            currentTemperature = params.initialTemperature,
            currentVolume = params.initialVolume,
            currentPressure = params.pressure,
            moleculePositions = positions,
            moleculeSpeeds = List(numMolecules) { 0f }
        )
    }
}
