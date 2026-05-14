package com.app.dopp.ui_project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.app.dopp.physics.*
import kotlin.math.*

@Composable
fun SimulationCanvas(
    experimentType: ExperimentType,
    pendulumState: PendulumState,
    pendulumParams: PendulumParameters,
    freeFallState: FreeFallState,
    freeFallParams: FreeFallParameters,
    collisionState: CollisionState,
    collisionParams: CollisionParameters,
    circuitState: CircuitState,
    magneticFieldState: MagneticFieldState,
    refractionState: RefractionState,
    refractionParams: RefractionParameters,
    lensState: LensState,
    lensParams: LensParameters,
    brownianState: BrownianMotionState,
    gasExpansionState: GasExpansionState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        when (experimentType) {
            ExperimentType.PENDULUM         -> drawPendulum(pendulumState, pendulumParams)
            ExperimentType.FREE_FALL        -> drawFreeFall(freeFallState, freeFallParams)
            ExperimentType.COLLISION        -> drawCollision(collisionState, collisionParams)
            ExperimentType.ELECTRIC_CIRCUIT -> drawCircuit(circuitState)
            ExperimentType.MAGNETIC_FIELD   -> drawMagneticField(magneticFieldState)
            ExperimentType.LIGHT_REFRACTION -> drawRefraction(refractionState, refractionParams)
            ExperimentType.LENS             -> drawLens(lensState, lensParams)
            ExperimentType.BROWNIAN_MOTION  -> drawBrownian(brownianState)
            ExperimentType.GAS_EXPANSION    -> drawGasExpansion(gasExpansionState, gasExpansionState.currentVolume)
        }
    }
}

// ==================== PENDULUM ====================

private fun DrawScope.drawPendulum(state: PendulumState, params: PendulumParameters) {
    val cx = size.width / 2f
    val pivotY = size.height * 0.12f
    val displayAngle = if (state.isRunning) state.currentAngle else params.initialAngle
    val ropeLen = (size.height * 0.60f) * (params.length / 3f).coerceIn(0.25f, 1f)

    val bobX = cx + ropeLen * sin(displayAngle)
    val bobY = pivotY + ropeLen * cos(displayAngle)

    // Ceiling rail
    drawLine(Color(0xFF90A4AE), Offset(cx - 60f, pivotY - 10f), Offset(cx + 60f, pivotY - 10f), 6f, cap = StrokeCap.Round)
    // Pivot pin
    drawCircle(Color(0xFF607D8B), 12f, Offset(cx, pivotY))
    // Rope
    drawLine(Color(0xFFE0E0E0), Offset(cx, pivotY), Offset(bobX, bobY), 3f)
    // Angle arc (shows displacement)
    if (displayAngle != 0f) {
        val arcR = ropeLen * 0.3f
        drawArc(
            color = Color(0xFF80DEEA).copy(alpha = 0.5f),
            startAngle = -90f,
            sweepAngle = Math.toDegrees(displayAngle.toDouble()).toFloat(),
            useCenter = false,
            topLeft = Offset(cx - arcR, pivotY - arcR),
            size = Size(arcR * 2, arcR * 2),
            style = Stroke(2f)
        )
    }
    // Bob shadow
    val bobR = (18f + params.mass * 4f).coerceIn(18f, 50f)
    drawCircle(Color.Black.copy(alpha = 0.3f), bobR + 4f, Offset(bobX + 4f, bobY + 4f))
    // Bob
    drawCircle(Color(0xFF1E88E5), bobR, Offset(bobX, bobY))
    drawCircle(Color(0xFF90CAF9), bobR * 0.45f, Offset(bobX - bobR * 0.25f, bobY - bobR * 0.25f))

    // Energy bar (if running)
    if (state.isRunning) {
        val totalE = state.kineticEnergy + state.potentialEnergy
        if (totalE > 0f) {
            val barW = 80f; val barH = 10f; val bx = 20f; val by = size.height - 60f
            drawRect(Color(0xFF37474F), Offset(bx, by), Size(barW, barH))
            val ke = ((state.kineticEnergy / totalE) * barW).coerceIn(0f, barW)
            drawRect(Color(0xFF42A5F5), Offset(bx, by), Size(ke, barH))
            drawRect(Color(0xFFEF5350), Offset(bx + ke, by), Size(barW - ke, barH))
        }
    }
}

// ==================== FREE FALL ====================

private fun DrawScope.drawFreeFall(state: FreeFallState, params: FreeFallParameters) {
    val cx = size.width / 2f
    val groundY = size.height * 0.88f
    val skyY = size.height * 0.06f
    val displayH = if (state.isRunning || state.currentHeight > 0f) state.currentHeight else params.initialHeight

    // Sky gradient blocks
    drawRect(Color(0xFF1A237E).copy(alpha = 0.2f), Offset(0f, 0f), Size(size.width, groundY))
    // Ground
    drawRect(Color(0xFF6D4C41), Offset(0f, groundY), Size(size.width, size.height - groundY))
    drawLine(Color(0xFF4E342E), Offset(0f, groundY), Offset(size.width, groundY), 4f)

    // Ruler (right side)
    val rx = size.width * 0.8f
    drawLine(Color(0xFF90A4AE), Offset(rx, skyY), Offset(rx, groundY), 2f)
    val tickCount = 5
    for (i in 0..tickCount) {
        val ty = skyY + (groundY - skyY) * (i.toFloat() / tickCount)
        drawLine(Color(0xFF90A4AE), Offset(rx - 12f, ty), Offset(rx + 4f, ty), 2f)
        val label = params.initialHeight * (1f - i.toFloat() / tickCount)
    }

    // Ball
    val fraction = if (params.initialHeight > 0f) (1f - displayH / params.initialHeight).coerceIn(0f, 1f) else 1f
    val ballY = skyY + (groundY - skyY - 34f) * fraction
    drawCircle(Color.Black.copy(alpha = 0.25f), 36f, Offset(cx + 3f, ballY + 3f))
    drawCircle(Color(0xFFE53935), 32f, Offset(cx, ballY))
    drawCircle(Color(0xFFFFCDD2), 14f, Offset(cx - 10f, ballY - 10f))

    // Velocity arrow (downward)
    if (state.isRunning && abs(state.currentVelocity) > 0.5f) {
        val arrowLen = (abs(state.currentVelocity) / params.initialHeight * 80f).coerceIn(10f, 100f)
        val ax = cx + 50f
        drawLine(Color(0xFFFFA000), Offset(ax, ballY + 34f), Offset(ax, ballY + 34f + arrowLen), 4f, cap = StrokeCap.Round)
        drawLine(Color(0xFFFFA000), Offset(ax - 8f, ballY + 34f + arrowLen - 12f), Offset(ax, ballY + 34f + arrowLen), 4f)
        drawLine(Color(0xFFFFA000), Offset(ax + 8f, ballY + 34f + arrowLen - 12f), Offset(ax, ballY + 34f + arrowLen), 4f)
    }

    // Landing burst
    if (state.hasLanded) {
        for (a in 0 until 8) {
            val angle = a * PI.toFloat() / 4f
            drawLine(Color(0xFFFF7043), Offset(cx, groundY - 2f), Offset(cx + cos(angle) * 30f, groundY - 2f + sin(angle) * 30f), 3f)
        }
    }
}

// ==================== COLLISION ====================

private fun DrawScope.drawCollision(state: CollisionState, params: CollisionParameters) {
    val cy = size.height * 0.48f
    val cx = size.width / 2f
    val scale = size.width / 12f  // positions in [-6, +6] range

    // Track
    drawLine(Color(0xFF455A64), Offset(30f, cy + 44f), Offset(size.width - 30f, cy + 44f), 4f)
    // Track shadow
    drawRect(Color(0xFF37474F).copy(alpha = 0.2f), Offset(30f, cy + 44f), Size(size.width - 60f, 20f))

    val r1 = (22f + params.mass1 * 5f).coerceIn(22f, 58f)
    val r2 = (22f + params.mass2 * 5f).coerceIn(22f, 58f)
    val x1 = cx + state.position1 * scale
    val x2 = cx + state.position2 * scale

    // Velocity arrows
    fun drawArrow(x: Float, vel: Float, r: Float, col: Color) {
        if (abs(vel) < 0.1f) return
        val len = (vel * scale * 0.5f).coerceIn(-100f, 100f)
        val startX = if (vel > 0) x + r else x - r
        val endX = startX + len
        drawLine(col.copy(alpha = 0.8f), Offset(startX, cy), Offset(endX, cy), 4f, cap = StrokeCap.Round)
        val dir = sign(len)
        drawLine(col, Offset(endX, cy), Offset(endX - dir * 12f, cy - 10f), 3f)
        drawLine(col, Offset(endX, cy), Offset(endX - dir * 12f, cy + 10f), 3f)
    }
    drawArrow(x1, state.currentVelocity1, r1, Color(0xFF42A5F5))
    drawArrow(x2, state.currentVelocity2, r2, Color(0xFFEF9A9A))

    // Ball 1 (blue)
    drawCircle(Color.Black.copy(alpha = 0.2f), r1 + 3f, Offset(x1 + 3f, cy + 44f - r1))
    drawCircle(Color(0xFF1E88E5), r1, Offset(x1, cy))
    drawCircle(Color(0xFF90CAF9), r1 * 0.4f, Offset(x1 - r1 * 0.25f, cy - r1 * 0.25f))

    // Ball 2 (red)
    drawCircle(Color.Black.copy(alpha = 0.2f), r2 + 3f, Offset(x2 + 3f, cy + 44f - r2))
    drawCircle(Color(0xFFE53935), r2, Offset(x2, cy))
    drawCircle(Color(0xFFEF9A9A), r2 * 0.4f, Offset(x2 - r2 * 0.25f, cy - r2 * 0.25f))

    // Collision flash
    if (state.hasCollided && abs(state.currentVelocity1 - state.currentVelocity2) < 0.2f) {
        val fx = (x1 + x2) / 2f
        drawCircle(Color(0xFFFFEB3B).copy(alpha = 0.4f), 70f, Offset(fx, cy))
    }
}

// ==================== ELECTRIC CIRCUIT ====================

private fun DrawScope.drawCircuit(state: CircuitState) {
    val m = 48f
    val left = m; val right = size.width - m
    val top = m * 1.5f; val bottom = size.height - m * 2f
    val sw = 4f

    // Wire frame
    val wireColor = Color(0xFF90A4AE)
    drawLine(wireColor, Offset(left, top), Offset(right, top), sw)
    drawLine(wireColor, Offset(right, top), Offset(right, bottom), sw)
    drawLine(wireColor, Offset(right, bottom), Offset(left, bottom), sw)
    drawLine(wireColor, Offset(left, bottom), Offset(left, top), sw)

    // Battery (left side)
    val bMid = (top + bottom) / 2f
    drawLine(Color(0xFFEF5350), Offset(left - 18f, bMid - 22f), Offset(left - 18f, bMid + 22f), 7f)
    drawLine(Color(0xFF78909C), Offset(left - 28f, bMid - 12f), Offset(left - 28f, bMid + 12f), 4f)

    // Resistors as zigzag rectangles
    fun drawResistor(cx: Float, cy: Float, horizontal: Boolean) {
        val hw = 28f; val hh = 9f
        if (horizontal) {
            drawRect(Color(0xFF607D8B), Offset(cx - hw, cy - hh), Size(hw * 2, hh * 2))
            // Zigzag
            val path = Path().apply {
                moveTo(cx - hw, cy)
                var x = cx - hw
                val step = hw * 2f / 5f
                for (i in 1..5) {
                    x += step
                    lineTo(x, if (i % 2 == 0) cy - hh + 3f else cy + hh - 3f)
                }
            }
            drawPath(path, Color(0xFFCFD8DC), style = Stroke(2f))
        } else {
            drawRect(Color(0xFF607D8B), Offset(cx - hh, cy - hw), Size(hh * 2, hw * 2))
            val path = Path().apply {
                moveTo(cx, cy - hw)
                var y = cy - hw
                val step = hw * 2f / 5f
                for (i in 1..5) {
                    y += step
                    lineTo(if (i % 2 == 0) cx - hh + 3f else cx + hh - 3f, y)
                }
            }
            drawPath(path, Color(0xFFCFD8DC), style = Stroke(2f))
        }
    }
    drawResistor((left + right) / 2f, top, true)
    drawResistor(right, (top + bottom) / 2f, false)
    drawResistor((left + right) / 2f, bottom, true)

    // Animated electrons
    val electronColor = Color(0xFFFFEB3B)
    val phase = state.electronFlowPhase
    val numElectrons = 8
    for (i in 0 until numElectrons) {
        val t = ((phase + i.toFloat() / numElectrons) % 1f)
        val pos = circuitPosition(t, left, right, top, bottom)
        drawCircle(electronColor, 6f, pos)
        drawCircle(electronColor.copy(alpha = 0.3f), 10f, pos)
    }
}

private fun circuitPosition(t: Float, l: Float, r: Float, top: Float, btm: Float): Offset {
    val w = r - l; val h = btm - top
    val perim = 2f * (w + h)
    val d = (t * perim) % perim
    return when {
        d < w            -> Offset(l + d, top)
        d < w + h        -> Offset(r, top + d - w)
        d < 2 * w + h    -> Offset(r - (d - w - h), btm)
        else             -> Offset(l, btm - (d - 2 * w - h))
    }
}

// ==================== MAGNETIC FIELD ====================

private fun DrawScope.drawMagneticField(state: MagneticFieldState) {
    val cx = size.width / 2f; val cy = size.height / 2f
    val phase = state.animationPhase

    // Field lines (concentric circles)
    val radii = listOf(55f, 110f, 165f, 220f).filter { it < minOf(cx, cy) * 0.95f }
    for ((idx, r) in radii.withIndex()) {
        val alpha = 0.9f - idx * 0.18f
        drawCircle(Color(0xFF1565C0).copy(alpha = alpha), r, Offset(cx, cy), style = Stroke(2.5f))
        // Direction arrow at animated position
        val angle = phase * 2f * PI.toFloat()
        val ax = cx + r * cos(angle)
        val ay = cy + r * sin(angle)
        val perp = angle + PI.toFloat() / 2f
        drawLine(Color(0xFF42A5F5).copy(alpha = alpha),
            Offset(ax - cos(perp) * 12f, ay - sin(perp) * 12f),
            Offset(ax + cos(perp) * 12f, ay + sin(perp) * 12f), 3f, cap = StrokeCap.Round)
    }

    // Wire cross-section (dot = current toward viewer)
    drawCircle(Color(0xFF263238), 26f, Offset(cx, cy))
    drawCircle(Color(0xFFFF8A65), 10f, Offset(cx, cy))
}

// ==================== LIGHT REFRACTION ====================

private fun DrawScope.drawRefraction(state: RefractionState, params: RefractionParameters) {
    val cx = size.width / 2f
    val boundaryY = size.height * 0.5f
    val rayLen = boundaryY * 0.85f

    // Medium backgrounds
    drawRect(Color(0xFF0288D1).copy(alpha = 0.1f), Offset(0f, 0f), Size(size.width, boundaryY))
    drawRect(Color(0xFF01579B).copy(alpha = 0.22f), Offset(0f, boundaryY), Size(size.width, size.height - boundaryY))

    // Boundary
    drawLine(Color(0xFF80DEEA), Offset(0f, boundaryY), Offset(size.width, boundaryY), 3f)

    // Normal (dashed)
    var dy = boundaryY - 180f
    while (dy < boundaryY + 180f) {
        drawLine(Color(0xFF607D8B).copy(alpha = 0.6f), Offset(cx, dy), Offset(cx, (dy + 22f).coerceAtMost(boundaryY + 180f)), 2f)
        dy += 36f
    }

    // Incident ray
    val ia = params.incidentAngle
    val startX = cx - rayLen * sin(ia)
    val startY = boundaryY - rayLen * cos(ia)
    drawLine(Color(0xFFFDD835), Offset(startX, startY), Offset(cx, boundaryY), 4f, cap = StrokeCap.Round)

    // Angle arc
    drawArc(Color(0xFFFDD835).copy(alpha = 0.4f), -90f, -Math.toDegrees(ia.toDouble()).toFloat(),
        false, Offset(cx - 50f, boundaryY - 50f), Size(100f, 100f), style = Stroke(2f))

    // Refracted / TIR ray
    if (state.isTotalInternalReflection) {
        val rx = cx + rayLen * sin(ia); val ry = boundaryY - rayLen * cos(ia)
        drawLine(Color(0xFFFF7043), Offset(cx, boundaryY), Offset(rx, ry), 4f, cap = StrokeCap.Round)
    } else {
        val ra = state.refractedAngle ?: 0f
        val rx = cx + rayLen * sin(ra); val ry = boundaryY + rayLen * cos(ra)
        drawLine(Color(0xFF81C784), Offset(cx, boundaryY), Offset(rx, ry), 4f, cap = StrokeCap.Round)
        drawArc(Color(0xFF81C784).copy(alpha = 0.4f), 90f, Math.toDegrees(ra.toDouble()).toFloat(),
            false, Offset(cx - 50f, boundaryY - 50f), Size(100f, 100f), style = Stroke(2f))
    }
}

// ==================== LENS ====================

private fun DrawScope.drawLens(state: LensState, params: LensParameters) {
    val cx = size.width / 2f; val cy = size.height / 2f
    val scale = size.width * 0.10f

    // Optical axis
    drawLine(Color(0xFF607D8B).copy(alpha = 0.5f), Offset(0f, cy), Offset(size.width, cy), 2f)

    // Focal point markers
    val f = params.focalLength * scale
    drawCircle(Color(0xFFFDD835), 7f, Offset(cx + f, cy))
    drawCircle(Color(0xFFFDD835), 7f, Offset(cx - f, cy))

    // Lens body
    val lh = 130f; val lw = 16f
    if (params.isConverging) {
        val path = Path().apply {
            moveTo(cx, cy - lh)
            cubicTo(cx + lw * 2.5f, cy - lh / 2f, cx + lw * 2.5f, cy + lh / 2f, cx, cy + lh)
            cubicTo(cx - lw * 2.5f, cy + lh / 2f, cx - lw * 2.5f, cy - lh / 2f, cx, cy - lh)
            close()
        }
        drawPath(path, Color(0xFF80DEEA).copy(alpha = 0.35f))
        drawPath(path, Color(0xFF0288D1), style = Stroke(2.5f))
    } else {
        val path = Path().apply {
            moveTo(cx, cy - lh)
            cubicTo(cx - lw * 2f, cy - lh / 2f, cx - lw * 2f, cy + lh / 2f, cx, cy + lh)
            cubicTo(cx + lw * 2f, cy + lh / 2f, cx + lw * 2f, cy - lh / 2f, cx, cy - lh)
            close()
        }
        drawPath(path, Color(0xFF80DEEA).copy(alpha = 0.25f))
        drawPath(path, Color(0xFF0288D1), style = Stroke(2.5f))
    }

    // Object (left of lens)
    val objX = (cx - params.objectDistance * scale).coerceAtLeast(30f)
    val objH = params.objectHeight * scale * 3f
    drawLine(Color(0xFFE53935), Offset(objX, cy), Offset(objX, cy - objH), 5f, cap = StrokeCap.Round)
    drawLine(Color(0xFFE53935), Offset(objX - 10f, cy - objH + 14f), Offset(objX, cy - objH), 4f)
    drawLine(Color(0xFFE53935), Offset(objX + 10f, cy - objH + 14f), Offset(objX, cy - objH), 4f)

    // Principal rays (only when running)
    if (state.isRunning && state.imageDistance != 0f) {
        val prog = state.rayAnimationProgress
        val imgX = (cx + state.imageDistance * scale).coerceIn(30f, size.width - 30f)
        val imgH = state.imageHeight * scale * 3f * if (state.isInverted) -1f else 1f

        // Ray 1: parallel to axis → through focal point
        val r1EndX = cx + f + (imgX - cx - f) * prog
        val r1EndY = cy - imgH * ((r1EndX - (cx + f)) / (imgX - cx - f)).coerceIn(0f, 1f)
        drawLine(Color(0xFFFF8A65).copy(alpha = 0.8f), Offset(objX, cy - objH), Offset(cx, cy - objH), 2.5f)
        drawLine(Color(0xFFFF8A65).copy(alpha = 0.8f), Offset(cx, cy - objH), Offset(imgX * prog + cx * (1f - prog), cy - imgH * prog), 2.5f)

        // Ray 2: through center straight
        drawLine(Color(0xFF80CBC4).copy(alpha = 0.8f), Offset(objX, cy - objH), Offset(imgX * prog + objX * (1f - prog), cy - imgH * prog), 2.5f)

        // Image
        if (!state.isVirtual) {
            drawLine(Color(0xFF81C784), Offset(imgX, cy), Offset(imgX, cy - imgH), 5f, cap = StrokeCap.Round)
            drawLine(Color(0xFF81C784), Offset(imgX - 10f, cy - imgH + 14f * sign(imgH)), Offset(imgX, cy - imgH), 4f)
            drawLine(Color(0xFF81C784), Offset(imgX + 10f, cy - imgH + 14f * sign(imgH)), Offset(imgX, cy - imgH), 4f)
        }
    }
}

// ==================== BROWNIAN MOTION ====================

private fun DrawScope.drawBrownian(state: BrownianMotionState) {
    // Liquid background
    drawRect(Color(0xFF01579B).copy(alpha = 0.12f), Offset(0f, 0f), Size(size.width, size.height))

    // Map from [-1,1] to canvas
    fun toX(v: Float) = (v + 1f) / 2f * size.width
    fun toY(v: Float) = (v + 1f) / 2f * size.height

    // Trails
    for ((idx, trail) in state.particleTrails.withIndex()) {
        if (trail.size > 1) {
            for (i in 1 until trail.size) {
                val a = trail[i - 1]; val b = trail[i]
                val alpha = (i.toFloat() / trail.size) * 0.35f
                drawLine(Color(0xFF42A5F5).copy(alpha = alpha),
                    Offset(toX(a.first), toY(a.second)),
                    Offset(toX(b.first), toY(b.second)), 1.5f)
            }
        }
    }

    // Particles
    for ((idx, pos) in state.particlePositions.withIndex()) {
        val x = toX(pos.first).coerceIn(10f, size.width - 10f)
        val y = toY(pos.second).coerceIn(10f, size.height - 10f)
        if (idx == 0) {
            // Large "pollen" particle
            drawCircle(Color(0xFF0D47A1).copy(alpha = 0.3f), 26f, Offset(x, y))
            drawCircle(Color(0xFF1565C0), 20f, Offset(x, y))
            drawCircle(Color(0xFF5C6BC0), 8f, Offset(x - 5f, y - 5f))
        } else {
            drawCircle(Color(0xFF80DEEA).copy(alpha = 0.7f), 5f, Offset(x, y))
        }
    }
}

// ==================== GAS EXPANSION ====================

private fun DrawScope.drawGasExpansion(state: GasExpansionState, volume: Float) {
    if (volume <= 0f || state.moleculePositions.isEmpty()) {
        drawRect(Color(0xFF37474F).copy(alpha = 0.3f), Offset(size.width * 0.15f, size.height * 0.2f),
            Size(size.width * 0.7f, size.height * 0.6f), style = Stroke(4f))
        return
    }

    // Container grows with volume
    val containerScale = sqrt(volume / 1f).coerceIn(1f, 2f)
    val baseW = size.width * 0.55f; val baseH = size.height * 0.55f
    val cW = (baseW * containerScale).coerceAtMost(size.width * 0.88f)
    val cH = (baseH * containerScale).coerceAtMost(size.height * 0.82f)
    val cLeft = (size.width - cW) / 2f; val cTop = (size.height - cH) / 2f

    // Container fill (semi-transparent)
    drawRect(Color(0xFF263238).copy(alpha = 0.2f), Offset(cLeft, cTop), Size(cW, cH))
    // Container walls
    drawRect(Color(0xFF80CBC4), Offset(cLeft, cTop), Size(cW, cH), style = Stroke(4f))

    // Molecules
    val inRange = (-containerScale)..(containerScale)
    for ((idx, mol) in state.moleculePositions.withIndex()) {
        val relX = ((mol.first + containerScale) / (containerScale * 2f)).coerceIn(0f, 1f)
        val relY = ((mol.second + containerScale) / (containerScale * 2f)).coerceIn(0f, 1f)
        val mx = cLeft + relX * cW
        val my = cTop + relY * cH
        val speed = state.moleculeSpeeds.getOrElse(idx) { 300f }
        val heat = ((speed - 200f) / 600f).coerceIn(0f, 1f)
        val molColor = Color(
            red = (0.2f + heat * 0.8f),
            green = (0.6f - heat * 0.3f),
            blue = (1f - heat * 0.8f)
        )
        drawCircle(molColor.copy(alpha = 0.9f), 7f, Offset(mx, my))
    }

    // Temperature label hint (color bar)
    val tempFraction = if (state.currentTemperature > 0f)
        ((state.currentTemperature - 200f) / 400f).coerceIn(0f, 1f)
    else 0f
    val barLeft = cLeft; val barY = cTop + cH + 18f; val barW = cW
    drawRect(Color(0xFF0D47A1), Offset(barLeft, barY), Size(barW * (1f - tempFraction), 8f))
    drawRect(Color(0xFFB71C1C), Offset(barLeft + barW * (1f - tempFraction), barY), Size(barW * tempFraction, 8f))
}
