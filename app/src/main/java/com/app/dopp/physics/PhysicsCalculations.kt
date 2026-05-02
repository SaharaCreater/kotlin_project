package com.app.dopp.physics

import kotlin.math.*

/**
 * Physics calculations for all experiments
 */
object PhysicsCalculations {
    
    const val GRAVITY = 9.81f // m/s²
    const val SPEED_OF_LIGHT = 299792458f // m/s
    const val BOLTZMANN_CONSTANT = 1.380649e-23f // J/K
    
    // ==================== MECHANICS ====================
    
    /**
     * Simple pendulum motion
     * θ(t) = θ₀ * cos(√(g/L) * t)
     */
    fun pendulumAngle(
        initialAngle: Float, // radians
        length: Float, // meters
        time: Float, // seconds
        damping: Float = 0.02f
    ): Float {
        val omega = sqrt(GRAVITY / length)
        val dampingFactor = exp(-damping * time)
        return initialAngle * cos(omega * time) * dampingFactor
    }
    
    /**
     * Pendulum angular velocity
     * ω(t) = -θ₀ * √(g/L) * sin(√(g/L) * t)
     */
    fun pendulumAngularVelocity(
        initialAngle: Float,
        length: Float,
        time: Float,
        damping: Float = 0.02f
    ): Float {
        val omega = sqrt(GRAVITY / length)
        val dampingFactor = exp(-damping * time)
        return -initialAngle * omega * sin(omega * time) * dampingFactor
    }
    
    /**
     * Pendulum period
     * T = 2π * √(L/g)
     */
    fun pendulumPeriod(length: Float): Float {
        return 2f * PI.toFloat() * sqrt(length / GRAVITY)
    }
    
    /**
     * Free fall position
     * y(t) = y₀ + v₀*t + 0.5*g*t²
     */
    fun freeFallPosition(
        initialHeight: Float,
        initialVelocity: Float,
        time: Float
    ): Float {
        return initialHeight + initialVelocity * time - 0.5f * GRAVITY * time * time
    }
    
    /**
     * Free fall velocity
     * v(t) = v₀ + g*t
     */
    fun freeFallVelocity(
        initialVelocity: Float,
        time: Float
    ): Float {
        return initialVelocity - GRAVITY * time
    }
    
    /**
     * Time to fall from height h
     * t = √(2h/g)
     */
    fun fallTime(height: Float): Float {
        return sqrt(2f * height / GRAVITY)
    }
    
    /**
     * Elastic collision - final velocities
     * v1' = ((m1-m2)/(m1+m2)) * v1 + (2*m2/(m1+m2)) * v2
     * v2' = (2*m1/(m1+m2)) * v1 + ((m2-m1)/(m1+m2)) * v2
     */
    fun elasticCollision(
        m1: Float, v1: Float,
        m2: Float, v2: Float
    ): Pair<Float, Float> {
        val totalMass = m1 + m2
        val v1Final = ((m1 - m2) / totalMass) * v1 + (2f * m2 / totalMass) * v2
        val v2Final = (2f * m1 / totalMass) * v1 + ((m2 - m1) / totalMass) * v2
        return Pair(v1Final, v2Final)
    }
    
    /**
     * Inelastic collision - final velocity (objects stick together)
     * v' = (m1*v1 + m2*v2) / (m1 + m2)
     */
    fun inelasticCollision(
        m1: Float, v1: Float,
        m2: Float, v2: Float
    ): Float {
        return (m1 * v1 + m2 * v2) / (m1 + m2)
    }
    
    /**
     * Kinetic energy
     * KE = 0.5 * m * v²
     */
    fun kineticEnergy(mass: Float, velocity: Float): Float {
        return 0.5f * mass * velocity * velocity
    }
    
    /**
     * Momentum
     * p = m * v
     */
    fun momentum(mass: Float, velocity: Float): Float {
        return mass * velocity
    }
    
    // ==================== ELECTRICITY & MAGNETISM ====================
    
    /**
     * Ohm's Law
     * V = I * R
     */
    fun voltage(current: Float, resistance: Float): Float {
        return current * resistance
    }
    
    fun current(voltage: Float, resistance: Float): Float {
        return if (resistance > 0f) voltage / resistance else 0f
    }
    
    /**
     * Series resistance
     * R_total = R1 + R2 + ... + Rn
     */
    fun seriesResistance(vararg resistances: Float): Float {
        return resistances.sum()
    }
    
    /**
     * Parallel resistance
     * 1/R_total = 1/R1 + 1/R2 + ... + 1/Rn
     */
    fun parallelResistance(vararg resistances: Float): Float {
        val sum = resistances.sumOf { if (it > 0f) 1.0 / it else 0.0 }
        return if (sum > 0) (1.0 / sum).toFloat() else 0f
    }
    
    /**
     * Electric power
     * P = V * I = I² * R = V² / R
     */
    fun electricPower(voltage: Float, current: Float): Float {
        return voltage * current
    }
    
    /**
     * Magnetic field from current-carrying wire
     * B = μ₀ * I / (2π * r)
     */
    fun magneticFieldWire(current: Float, distance: Float): Float {
        val mu0 = 4f * PI.toFloat() * 1e-7f // permeability of free space
        return if (distance > 0f) mu0 * current / (2f * PI.toFloat() * distance) else 0f
    }
    
    /**
     * Lorentz force
     * F = q * v * B * sin(θ)
     */
    fun lorentzForce(charge: Float, velocity: Float, magneticField: Float, angle: Float): Float {
        return charge * velocity * magneticField * sin(angle)
    }
    
    // ==================== OPTICS ====================
    
    /**
     * Snell's Law - refraction angle
     * n1 * sin(θ1) = n2 * sin(θ2)
     * θ2 = arcsin(n1/n2 * sin(θ1))
     */
    fun refractionAngle(
        incidentAngle: Float, // radians
        n1: Float, // refractive index of medium 1
        n2: Float  // refractive index of medium 2
    ): Float? {
        val ratio = n1 / n2 * sin(incidentAngle)
        return if (abs(ratio) <= 1f) asin(ratio) else null // null means total internal reflection
    }
    
    /**
     * Critical angle for total internal reflection
     * θc = arcsin(n2/n1) where n1 > n2
     */
    fun criticalAngle(n1: Float, n2: Float): Float? {
        return if (n1 > n2) asin(n2 / n1) else null
    }
    
    /**
     * Thin lens equation
     * 1/f = 1/do + 1/di
     * di = (f * do) / (do - f)
     */
    fun imageDistance(objectDistance: Float, focalLength: Float): Float {
        return if (objectDistance != focalLength) {
            (focalLength * objectDistance) / (objectDistance - focalLength)
        } else Float.POSITIVE_INFINITY
    }
    
    /**
     * Magnification
     * M = -di / do = hi / ho
     */
    fun magnification(objectDistance: Float, imageDistance: Float): Float {
        return if (objectDistance != 0f) -imageDistance / objectDistance else 0f
    }
    
    /**
     * Lens maker's equation (thin lens)
     * 1/f = (n-1) * (1/R1 - 1/R2)
     */
    fun focalLength(refractiveIndex: Float, r1: Float, r2: Float): Float {
        val term = (1f / r1) - (1f / r2)
        return if (term != 0f) 1f / ((refractiveIndex - 1f) * term) else Float.POSITIVE_INFINITY
    }
    
    // ==================== THERMODYNAMICS ====================
    
    /**
     * Ideal gas law
     * PV = nRT
     * P = nRT/V
     */
    fun idealGasPressure(moles: Float, temperature: Float, volume: Float): Float {
        val R = 8.314f // Gas constant J/(mol·K)
        return if (volume > 0f) moles * R * temperature / volume else 0f
    }
    
    /**
     * Brownian motion - mean square displacement
     * <x²> = 2Dt
     * D = kT / (6πηr)
     */
    fun brownianDisplacement(
        temperature: Float, // Kelvin
        viscosity: Float, // Pa·s
        particleRadius: Float, // meters
        time: Float // seconds
    ): Float {
        val D = BOLTZMANN_CONSTANT * temperature / (6f * PI.toFloat() * viscosity * particleRadius)
        return sqrt(2f * D * time)
    }
    
    /**
     * Random Brownian step (for simulation)
     */
    fun brownianStep(
        temperature: Float,
        viscosity: Float,
        particleRadius: Float,
        deltaTime: Float
    ): Pair<Float, Float> {
        val sigma = brownianDisplacement(temperature, viscosity, particleRadius, deltaTime)
        val random = java.util.Random()
        val dx = (random.nextGaussian() * sigma).toFloat()
        val dy = (random.nextGaussian() * sigma).toFloat()
        return Pair(dx, dy)
    }
    
    /**
     * Thermal expansion
     * ΔL = L₀ * α * ΔT
     * L = L₀ * (1 + α * ΔT)
     */
    fun thermalExpansion(
        initialLength: Float,
        coefficient: Float, // thermal expansion coefficient
        temperatureChange: Float
    ): Float {
        return initialLength * (1f + coefficient * temperatureChange)
    }
    
    /**
     * Heat transfer
     * Q = m * c * ΔT
     */
    fun heatTransfer(mass: Float, specificHeat: Float, temperatureChange: Float): Float {
        return mass * specificHeat * temperatureChange
    }
    
    /**
     * Maxwell-Boltzmann most probable speed
     * v_p = √(2kT/m)
     */
    fun mostProbableSpeed(temperature: Float, molecularMass: Float): Float {
        return sqrt(2f * BOLTZMANN_CONSTANT * temperature / molecularMass)
    }
    
    /**
     * RMS speed of gas molecules
     * v_rms = √(3kT/m)
     */
    fun rmsSpeed(temperature: Float, molecularMass: Float): Float {
        return sqrt(3f * BOLTZMANN_CONSTANT * temperature / molecularMass)
    }
}
