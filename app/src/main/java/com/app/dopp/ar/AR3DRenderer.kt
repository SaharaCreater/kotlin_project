package com.app.dopp.ar

import android.content.Context
import android.graphics.Color
import android.opengl.Matrix
import com.app.dopp.physics.*
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlin.math.*

/**
 * AR 3D Renderer that creates procedural 3D objects for physics experiments
 */
class AR3DRenderer(private val context: Context) {
    
    companion object {
        // Color palette for different elements
        val COLOR_PRIMARY = floatArrayOf(0.2f, 0.6f, 1.0f, 1.0f) // Blue
        val COLOR_SECONDARY = floatArrayOf(1.0f, 0.4f, 0.2f, 1.0f) // Orange
        val COLOR_ACCENT = floatArrayOf(0.3f, 0.9f, 0.4f, 1.0f) // Green
        val COLOR_NEUTRAL = floatArrayOf(0.7f, 0.7f, 0.7f, 1.0f) // Gray
        val COLOR_WIRE = floatArrayOf(0.8f, 0.6f, 0.2f, 1.0f) // Gold/Copper
        val COLOR_LIGHT = floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f) // Yellow
        val COLOR_GLASS = floatArrayOf(0.8f, 0.9f, 1.0f, 0.5f) // Translucent blue
        val COLOR_HEAT_COLD = floatArrayOf(0.2f, 0.4f, 1.0f, 1.0f) // Cold blue
        val COLOR_HEAT_HOT = floatArrayOf(1.0f, 0.3f, 0.1f, 1.0f) // Hot red
    }
    
    /**
     * Create pendulum visualization
     */
    fun createPendulumNodes(
        state: PendulumState,
        params: PendulumParameters,
        scale: Float = 0.3f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Pivot point (fixed)
        nodes.add(NodeData(
            id = "pivot",
            position = Position(0f, 0f, 0f),
            scale = Scale(0.05f * scale, 0.05f * scale, 0.05f * scale),
            color = COLOR_NEUTRAL,
            shape = Shape.SPHERE
        ))
        
        // Calculate bob position based on angle
        val rodLength = params.length * scale
        val bobX = sin(state.currentAngle) * rodLength
        val bobY = -cos(state.currentAngle) * rodLength
        
        // Rod (line from pivot to bob)
        val rodCenterX = bobX / 2f
        val rodCenterY = bobY / 2f
        nodes.add(NodeData(
            id = "rod",
            position = Position(rodCenterX, rodCenterY, 0f),
            scale = Scale(0.01f * scale, rodLength / 2f, 0.01f * scale),
            rotation = Rotation(0f, 0f, Math.toDegrees(state.currentAngle.toDouble()).toFloat()),
            color = COLOR_NEUTRAL,
            shape = Shape.CYLINDER
        ))
        
        // Bob (pendulum mass)
        val bobSize = 0.08f * scale * sqrt(params.mass)
        nodes.add(NodeData(
            id = "bob",
            position = Position(bobX, bobY, 0f),
            scale = Scale(bobSize, bobSize, bobSize),
            color = COLOR_PRIMARY,
            shape = Shape.SPHERE
        ))
        
        // Velocity indicator arrow
        if (abs(state.angularVelocity) > 0.01f) {
            val arrowLength = abs(state.angularVelocity) * 0.1f * scale
            val arrowDirection = if (state.angularVelocity > 0) 1f else -1f
            nodes.add(NodeData(
                id = "velocity_arrow",
                position = Position(bobX + arrowDirection * arrowLength / 2f, bobY, 0f),
                scale = Scale(arrowLength, 0.02f * scale, 0.02f * scale),
                color = COLOR_ACCENT,
                shape = Shape.ARROW
            ))
        }
        
        return nodes
    }
    
    /**
     * Create free fall visualization
     */
    fun createFreeFallNodes(
        state: FreeFallState,
        params: FreeFallParameters,
        scale: Float = 0.1f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Ground plane
        nodes.add(NodeData(
            id = "ground",
            position = Position(0f, 0f, 0f),
            scale = Scale(0.5f, 0.02f, 0.5f),
            color = COLOR_NEUTRAL,
            shape = Shape.BOX
        ))
        
        // Falling object
        val objectSize = 0.05f * sqrt(params.mass).coerceIn(0.03f, 0.15f)
        val objectY = state.currentHeight * scale
        nodes.add(NodeData(
            id = "falling_object",
            position = Position(0f, objectY, 0f),
            scale = Scale(objectSize, objectSize, objectSize),
            color = if (state.hasLanded) COLOR_ACCENT else COLOR_PRIMARY,
            shape = Shape.SPHERE
        ))
        
        // Height markers
        val markerCount = 5
        val markerSpacing = params.initialHeight * scale / markerCount
        for (i in 0..markerCount) {
            val markerY = i * markerSpacing
            nodes.add(NodeData(
                id = "marker_$i",
                position = Position(0.3f, markerY, 0f),
                scale = Scale(0.1f, 0.005f, 0.005f),
                color = COLOR_NEUTRAL,
                shape = Shape.BOX
            ))
        }
        
        // Velocity vector (downward arrow)
        if (abs(state.currentVelocity) > 0.5f && !state.hasLanded) {
            val arrowLength = minOf(abs(state.currentVelocity) * scale * 0.05f, 0.2f)
            val arrowY = objectY - arrowLength / 2f - objectSize
            nodes.add(NodeData(
                id = "velocity_arrow",
                position = Position(0f, arrowY, 0f),
                scale = Scale(0.02f, arrowLength, 0.02f),
                rotation = Rotation(0f, 0f, 180f),
                color = COLOR_SECONDARY,
                shape = Shape.ARROW
            ))
        }
        
        return nodes
    }
    
    /**
     * Create collision visualization
     */
    fun createCollisionNodes(
        state: CollisionState,
        params: CollisionParameters,
        scale: Float = 0.15f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Track
        nodes.add(NodeData(
            id = "track",
            position = Position(0f, -0.05f * scale, 0f),
            scale = Scale(1.0f, 0.02f, 0.1f),
            color = COLOR_NEUTRAL,
            shape = Shape.BOX
        ))
        
        // Ball 1
        val ball1Size = 0.1f * scale * sqrt(params.mass1).coerceIn(0.5f, 2f)
        nodes.add(NodeData(
            id = "ball1",
            position = Position(state.position1 * scale, ball1Size, 0f),
            scale = Scale(ball1Size, ball1Size, ball1Size),
            color = COLOR_PRIMARY,
            shape = Shape.SPHERE
        ))
        
        // Ball 2
        val ball2Size = 0.1f * scale * sqrt(params.mass2).coerceIn(0.5f, 2f)
        nodes.add(NodeData(
            id = "ball2",
            position = Position(state.position2 * scale, ball2Size, 0f),
            scale = Scale(ball2Size, ball2Size, ball2Size),
            color = COLOR_SECONDARY,
            shape = Shape.SPHERE
        ))
        
        // Velocity arrows
        if (abs(state.currentVelocity1) > 0.1f) {
            val arrow1Length = state.currentVelocity1 * scale * 0.05f
            nodes.add(NodeData(
                id = "velocity1",
                position = Position(state.position1 * scale + arrow1Length / 2f + ball1Size, ball1Size, 0f),
                scale = Scale(abs(arrow1Length), 0.02f, 0.02f),
                rotation = Rotation(0f, 0f, if (arrow1Length < 0) 180f else 0f),
                color = COLOR_PRIMARY,
                shape = Shape.ARROW
            ))
        }
        
        if (abs(state.currentVelocity2) > 0.1f) {
            val arrow2Length = state.currentVelocity2 * scale * 0.05f
            nodes.add(NodeData(
                id = "velocity2",
                position = Position(state.position2 * scale + arrow2Length / 2f + ball2Size, ball2Size, 0f),
                scale = Scale(abs(arrow2Length), 0.02f, 0.02f),
                rotation = Rotation(0f, 0f, if (arrow2Length < 0) 180f else 0f),
                color = COLOR_SECONDARY,
                shape = Shape.ARROW
            ))
        }
        
        // Collision indicator
        if (state.hasCollided) {
            val collisionX = (state.position1 + state.position2) / 2f * scale
            nodes.add(NodeData(
                id = "collision_marker",
                position = Position(collisionX, 0.3f, 0f),
                scale = Scale(0.05f, 0.05f, 0.05f),
                color = COLOR_ACCENT,
                shape = Shape.STAR
            ))
        }
        
        return nodes
    }
    
    /**
     * Create electric circuit visualization
     */
    fun createCircuitNodes(
        state: CircuitState,
        params: CircuitParameters,
        scale: Float = 0.2f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Battery
        nodes.add(NodeData(
            id = "battery",
            position = Position(-0.4f * scale, 0f, 0f),
            scale = Scale(0.08f * scale, 0.15f * scale, 0.05f * scale),
            color = COLOR_SECONDARY,
            shape = Shape.BOX
        ))
        
        // Wires and resistors
        if (params.isParallel) {
            // Parallel circuit layout
            val yOffsets = listOf(-0.2f, 0f, 0.2f)
            for (i in 0..2) {
                val resistance = when (i) {
                    0 -> params.resistance1
                    1 -> params.resistance2
                    else -> params.resistance3
                }
                val yPos = yOffsets[i] * scale
                
                // Resistor
                val resistorSize = 0.03f + (resistance / 1000f) * 0.02f
                nodes.add(NodeData(
                    id = "resistor_$i",
                    position = Position(0.1f * scale, yPos, 0f),
                    scale = Scale(0.1f * scale, resistorSize * scale, resistorSize * scale),
                    color = COLOR_WIRE,
                    shape = Shape.BOX
                ))
                
                // Wire segments
                nodes.add(NodeData(
                    id = "wire_left_$i",
                    position = Position(-0.15f * scale, yPos, 0f),
                    scale = Scale(0.15f * scale, 0.01f * scale, 0.01f * scale),
                    color = COLOR_WIRE,
                    shape = Shape.CYLINDER
                ))
                
                nodes.add(NodeData(
                    id = "wire_right_$i",
                    position = Position(0.3f * scale, yPos, 0f),
                    scale = Scale(0.15f * scale, 0.01f * scale, 0.01f * scale),
                    color = COLOR_WIRE,
                    shape = Shape.CYLINDER
                ))
            }
        } else {
            // Series circuit layout
            val xPositions = listOf(-0.1f, 0.1f, 0.3f)
            for (i in 0..2) {
                val resistance = when (i) {
                    0 -> params.resistance1
                    1 -> params.resistance2
                    else -> params.resistance3
                }
                val xPos = xPositions[i] * scale
                
                // Resistor
                val resistorSize = 0.03f + (resistance / 1000f) * 0.02f
                nodes.add(NodeData(
                    id = "resistor_$i",
                    position = Position(xPos, 0f, 0f),
                    scale = Scale(0.08f * scale, resistorSize * scale, resistorSize * scale),
                    color = COLOR_WIRE,
                    shape = Shape.BOX
                ))
            }
            
            // Connecting wires
            nodes.add(NodeData(
                id = "wire_main",
                position = Position(0.1f * scale, 0f, 0f),
                scale = Scale(0.6f * scale, 0.008f * scale, 0.008f * scale),
                color = COLOR_WIRE,
                shape = Shape.CYLINDER
            ))
        }
        
        // Electron flow animation (small moving spheres)
        val numElectrons = 5
        for (i in 0 until numElectrons) {
            val phase = (state.electronFlowPhase + i * (2f * PI.toFloat() / numElectrons)) % (2f * PI.toFloat())
            val electronX = -0.3f + (phase / (2f * PI.toFloat())) * 0.8f
            nodes.add(NodeData(
                id = "electron_$i",
                position = Position(electronX * scale, 0f, 0f),
                scale = Scale(0.015f * scale, 0.015f * scale, 0.015f * scale),
                color = COLOR_PRIMARY,
                shape = Shape.SPHERE
            ))
        }
        
        return nodes
    }
    
    /**
     * Create magnetic field visualization
     */
    fun createMagneticFieldNodes(
        state: MagneticFieldState,
        params: MagneticFieldParameters,
        scale: Float = 0.2f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Central wire (current-carrying conductor)
        nodes.add(NodeData(
            id = "wire",
            position = Position(0f, 0f, 0f),
            scale = Scale(0.02f * scale, 0.5f * scale, 0.02f * scale),
            color = COLOR_WIRE,
            shape = Shape.CYLINDER
        ))
        
        // Current direction indicator
        val currentDirection = if (params.current > 0) 1f else -1f
        nodes.add(NodeData(
            id = "current_arrow",
            position = Position(0f, 0.3f * scale * currentDirection, 0f),
            scale = Scale(0.02f * scale, 0.05f * scale, 0.02f * scale),
            rotation = Rotation(0f, 0f, if (currentDirection > 0) 0f else 180f),
            color = COLOR_SECONDARY,
            shape = Shape.ARROW
        ))
        
        // Magnetic field lines (concentric circles)
        if (params.showFieldLines) {
            val radii = listOf(0.1f, 0.2f, 0.35f, 0.5f)
            for ((index, radius) in radii.withIndex()) {
                val fieldStrength = PhysicsCalculations.magneticFieldWire(params.current, radius)
                val lineThickness = 0.005f + fieldStrength * 1e5f
                
                // Create multiple segments to form a circle
                val segments = params.numberOfLines
                for (i in 0 until segments) {
                    val angle1 = (2f * PI.toFloat() * i / segments) + state.animationPhase
                    val angle2 = (2f * PI.toFloat() * (i + 1) / segments) + state.animationPhase
                    
                    val x1 = cos(angle1) * radius * scale
                    val z1 = sin(angle1) * radius * scale
                    val x2 = cos(angle2) * radius * scale
                    val z2 = sin(angle2) * radius * scale
                    
                    val midX = (x1 + x2) / 2f
                    val midZ = (z1 + z2) / 2f
                    val segmentLength = sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1))
                    val segmentAngle = atan2(z2 - z1, x2 - x1)
                    
                    nodes.add(NodeData(
                        id = "field_line_${index}_$i",
                        position = Position(midX, 0f, midZ),
                        scale = Scale(segmentLength, lineThickness * scale, lineThickness * scale),
                        rotation = Rotation(0f, Math.toDegrees(segmentAngle.toDouble()).toFloat(), 0f),
                        color = COLOR_PRIMARY,
                        shape = Shape.CYLINDER
                    ))
                }
            }
        }
        
        return nodes
    }
    
    /**
     * Create light refraction visualization
     */
    fun createRefractionNodes(
        state: RefractionState,
        params: RefractionParameters,
        scale: Float = 0.3f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Medium 1 (top - air)
        nodes.add(NodeData(
            id = "medium1",
            position = Position(0f, 0.15f * scale, 0f),
            scale = Scale(0.6f * scale, 0.3f * scale, 0.02f * scale),
            color = floatArrayOf(0.9f, 0.95f, 1.0f, 0.3f),
            shape = Shape.BOX
        ))
        
        // Medium 2 (bottom - glass/water)
        val medium2Color = when {
            params.n2 > 1.4f -> COLOR_GLASS // Glass
            else -> floatArrayOf(0.7f, 0.85f, 1.0f, 0.4f) // Water
        }
        nodes.add(NodeData(
            id = "medium2",
            position = Position(0f, -0.15f * scale, 0f),
            scale = Scale(0.6f * scale, 0.3f * scale, 0.02f * scale),
            color = medium2Color,
            shape = Shape.BOX
        ))
        
        // Interface line
        nodes.add(NodeData(
            id = "interface",
            position = Position(0f, 0f, 0f),
            scale = Scale(0.6f * scale, 0.005f * scale, 0.02f * scale),
            color = COLOR_NEUTRAL,
            shape = Shape.BOX
        ))
        
        // Normal line
        if (params.showNormal) {
            nodes.add(NodeData(
                id = "normal",
                position = Position(0f, 0f, 0f),
                scale = Scale(0.005f * scale, 0.5f * scale, 0.005f * scale),
                color = floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f),
                shape = Shape.CYLINDER
            ))
        }
        
        // Incident ray
        val incidentLength = 0.25f * scale * state.rayAnimationProgress
        val incidentX = -sin(params.incidentAngle) * incidentLength
        val incidentY = cos(params.incidentAngle) * incidentLength + 0.25f * scale
        nodes.add(NodeData(
            id = "incident_ray",
            position = Position(incidentX / 2f, incidentY / 2f, 0f),
            scale = Scale(0.01f * scale, incidentLength, 0.01f * scale),
            rotation = Rotation(0f, 0f, Math.toDegrees(params.incidentAngle.toDouble()).toFloat()),
            color = COLOR_LIGHT,
            shape = Shape.CYLINDER
        ))
        
        // Refracted or reflected ray
        if (state.isTotalInternalReflection) {
            // Reflected ray (total internal reflection)
            val reflectAngle = params.incidentAngle
            val reflectLength = 0.2f * scale * maxOf(0f, state.rayAnimationProgress - 0.5f) * 2f
            val reflectX = sin(reflectAngle) * reflectLength
            val reflectY = cos(reflectAngle) * reflectLength
            nodes.add(NodeData(
                id = "reflected_ray",
                position = Position(reflectX / 2f, reflectY / 2f, 0f),
                scale = Scale(0.01f * scale, reflectLength, 0.01f * scale),
                rotation = Rotation(0f, 0f, -Math.toDegrees(reflectAngle.toDouble()).toFloat()),
                color = COLOR_LIGHT,
                shape = Shape.CYLINDER
            ))
        } else if (state.refractedAngle != null) {
            // Refracted ray
            val refractLength = 0.2f * scale * maxOf(0f, state.rayAnimationProgress - 0.5f) * 2f
            val refractX = sin(state.refractedAngle) * refractLength
            val refractY = -cos(state.refractedAngle) * refractLength
            nodes.add(NodeData(
                id = "refracted_ray",
                position = Position(refractX / 2f, refractY / 2f, 0f),
                scale = Scale(0.01f * scale, refractLength, 0.01f * scale),
                rotation = Rotation(0f, 0f, Math.toDegrees(state.refractedAngle.toDouble()).toFloat()),
                color = COLOR_LIGHT,
                shape = Shape.CYLINDER
            ))
        }
        
        return nodes
    }
    
    /**
     * Create lens visualization
     */
    fun createLensNodes(
        state: LensState,
        params: LensParameters,
        scale: Float = 0.2f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Optical axis
        nodes.add(NodeData(
            id = "optical_axis",
            position = Position(0f, 0f, 0f),
            scale = Scale(1.0f * scale, 0.003f * scale, 0.003f * scale),
            color = COLOR_NEUTRAL,
            shape = Shape.CYLINDER
        ))
        
        // Lens
        val lensThickness = if (params.isConverging) 0.03f else 0.01f
        nodes.add(NodeData(
            id = "lens",
            position = Position(0f, 0f, 0f),
            scale = Scale(lensThickness * scale, 0.3f * scale, 0.03f * scale),
            color = COLOR_GLASS,
            shape = if (params.isConverging) Shape.CONVEX_LENS else Shape.CONCAVE_LENS
        ))
        
        // Focal points
        val focalLength = params.focalLength * scale
        nodes.add(NodeData(
            id = "focal_point_right",
            position = Position(focalLength, 0f, 0f),
            scale = Scale(0.015f * scale, 0.015f * scale, 0.015f * scale),
            color = COLOR_ACCENT,
            shape = Shape.SPHERE
        ))
        nodes.add(NodeData(
            id = "focal_point_left",
            position = Position(-focalLength, 0f, 0f),
            scale = Scale(0.015f * scale, 0.015f * scale, 0.015f * scale),
            color = COLOR_ACCENT,
            shape = Shape.SPHERE
        ))
        
        // Object (arrow)
        val objectX = -params.objectDistance * scale
        nodes.add(NodeData(
            id = "object_arrow",
            position = Position(objectX, params.objectHeight * scale / 2f, 0f),
            scale = Scale(0.01f * scale, params.objectHeight * scale, 0.01f * scale),
            color = COLOR_PRIMARY,
            shape = Shape.ARROW
        ))
        
        // Image (if formed)
        if (state.rayAnimationProgress > 0.7f && !state.imageDistance.isInfinite()) {
            val imageX = state.imageDistance * scale
            val imageY = state.imageHeight * scale / 2f * (if (state.isInverted) -1f else 1f)
            val imageColor = if (state.isVirtual) {
                floatArrayOf(COLOR_SECONDARY[0], COLOR_SECONDARY[1], COLOR_SECONDARY[2], 0.5f)
            } else {
                COLOR_SECONDARY
            }
            nodes.add(NodeData(
                id = "image_arrow",
                position = Position(imageX, imageY, 0f),
                scale = Scale(0.01f * scale, state.imageHeight * scale, 0.01f * scale),
                rotation = Rotation(0f, 0f, if (state.isInverted) 180f else 0f),
                color = imageColor,
                shape = Shape.ARROW
            ))
        }
        
        // Light rays
        val rayProgress = state.rayAnimationProgress
        if (rayProgress > 0f) {
            // Parallel ray
            val parallelRayEndX = if (rayProgress < 0.5f) {
                objectX + (0f - objectX) * (rayProgress / 0.5f)
            } else {
                0f + (focalLength - 0f) * ((rayProgress - 0.5f) / 0.5f)
            }
            // Add ray visualization here (simplified)
        }
        
        return nodes
    }
    
    /**
     * Create Brownian motion visualization
     */
    fun createBrownianMotionNodes(
        state: BrownianMotionState,
        params: BrownianMotionParameters,
        scale: Float = 0.3f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Container
        nodes.add(NodeData(
            id = "container",
            position = Position(0f, 0f, 0f),
            scale = Scale(0.5f * scale, 0.5f * scale, 0.02f * scale),
            color = floatArrayOf(0.8f, 0.9f, 1.0f, 0.2f),
            shape = Shape.BOX
        ))
        
        // Particles
        val particleSize = 0.015f * scale
        for ((index, position) in state.particlePositions.withIndex()) {
            val (x, y) = position
            // Color based on temperature
            val tempFactor = (params.temperature - 200f) / 300f
            val particleColor = floatArrayOf(
                COLOR_HEAT_COLD[0] + (COLOR_HEAT_HOT[0] - COLOR_HEAT_COLD[0]) * tempFactor,
                COLOR_HEAT_COLD[1] + (COLOR_HEAT_HOT[1] - COLOR_HEAT_COLD[1]) * tempFactor,
                COLOR_HEAT_COLD[2] + (COLOR_HEAT_HOT[2] - COLOR_HEAT_COLD[2]) * tempFactor,
                1.0f
            )
            
            nodes.add(NodeData(
                id = "particle_$index",
                position = Position(x * 0.2f * scale, y * 0.2f * scale, 0f),
                scale = Scale(particleSize, particleSize, particleSize),
                color = particleColor,
                shape = Shape.SPHERE
            ))
        }
        
        // Particle trails (optional, last few positions)
        for ((particleIndex, trail) in state.particleTrails.withIndex()) {
            val recentTrail = trail.takeLast(10)
            for ((trailIndex, trailPos) in recentTrail.withIndex()) {
                val alpha = (trailIndex.toFloat() / recentTrail.size) * 0.3f
                nodes.add(NodeData(
                    id = "trail_${particleIndex}_$trailIndex",
                    position = Position(trailPos.first * 0.2f * scale, trailPos.second * 0.2f * scale, 0f),
                    scale = Scale(0.005f * scale, 0.005f * scale, 0.005f * scale),
                    color = floatArrayOf(0.5f, 0.5f, 0.5f, alpha),
                    shape = Shape.SPHERE
                ))
            }
        }
        
        return nodes
    }
    
    /**
     * Create gas expansion visualization
     */
    fun createGasExpansionNodes(
        state: GasExpansionState,
        params: GasExpansionParameters,
        scale: Float = 0.2f
    ): List<NodeData> {
        val nodes = mutableListOf<NodeData>()
        
        // Container (piston cylinder)
        val volumeScale = sqrt(state.currentVolume / params.initialVolume).coerceIn(0.8f, 1.5f)
        
        // Cylinder walls
        nodes.add(NodeData(
            id = "cylinder_base",
            position = Position(0f, -0.2f * scale, 0f),
            scale = Scale(0.3f * scale * volumeScale, 0.02f * scale, 0.3f * scale * volumeScale),
            color = COLOR_NEUTRAL,
            shape = Shape.CYLINDER
        ))
        
        // Piston (top, moves based on volume)
        val pistonY = 0.3f * scale * volumeScale
        nodes.add(NodeData(
            id = "piston",
            position = Position(0f, pistonY, 0f),
            scale = Scale(0.28f * scale * volumeScale, 0.03f * scale, 0.28f * scale * volumeScale),
            color = COLOR_WIRE,
            shape = Shape.CYLINDER
        ))
        
        // Gas molecules
        val tempFactor = ((state.currentTemperature - 200f) / 400f).coerceIn(0f, 1f)
        val moleculeColor = floatArrayOf(
            COLOR_HEAT_COLD[0] + (COLOR_HEAT_HOT[0] - COLOR_HEAT_COLD[0]) * tempFactor,
            COLOR_HEAT_COLD[1] + (COLOR_HEAT_HOT[1] - COLOR_HEAT_COLD[1]) * tempFactor,
            COLOR_HEAT_COLD[2] + (COLOR_HEAT_HOT[2] - COLOR_HEAT_COLD[2]) * tempFactor,
            1.0f
        )
        
        val moleculeSize = 0.01f * scale
        for ((index, position) in state.moleculePositions.withIndex()) {
            val (x, y, z) = position
            val scaledX = x * 0.12f * scale * volumeScale
            val scaledY = y * 0.2f * scale * volumeScale
            val scaledZ = z * 0.12f * scale * volumeScale
            
            nodes.add(NodeData(
                id = "molecule_$index",
                position = Position(scaledX, scaledY, scaledZ),
                scale = Scale(moleculeSize, moleculeSize, moleculeSize),
                color = moleculeColor,
                shape = Shape.SPHERE
            ))
        }
        
        // Temperature indicator (thermometer)
        val thermoHeight = 0.3f * scale
        val thermoFill = thermoHeight * tempFactor
        nodes.add(NodeData(
            id = "thermometer_tube",
            position = Position(0.25f * scale, 0f, 0f),
            scale = Scale(0.01f * scale, thermoHeight, 0.01f * scale),
            color = COLOR_NEUTRAL,
            shape = Shape.CYLINDER
        ))
        nodes.add(NodeData(
            id = "thermometer_fill",
            position = Position(0.25f * scale, -thermoHeight / 2f + thermoFill / 2f, 0f),
            scale = Scale(0.008f * scale, thermoFill, 0.008f * scale),
            color = COLOR_HEAT_HOT,
            shape = Shape.CYLINDER
        ))
        
        return nodes
    }
}

/**
 * Data class representing a 3D node to render
 */
data class NodeData(
    val id: String,
    val position: Position,
    val scale: Scale,
    val rotation: Rotation = Rotation(0f, 0f, 0f),
    val color: FloatArray,
    val shape: Shape
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as NodeData
        return id == other.id
    }
    
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Supported 3D shapes
 */
enum class Shape {
    SPHERE,
    BOX,
    CYLINDER,
    ARROW,
    STAR,
    CONVEX_LENS,
    CONCAVE_LENS
}
