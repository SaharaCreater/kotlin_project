package com.app.dopp.ui_project

import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.app.dopp.ar.AR3DRenderer
import com.app.dopp.physics.*
import com.app.dopp.ui_project.components.ExperimentInfoPanel
import com.app.dopp.ui_project.components.ParametersPanel
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    experimentType: ExperimentType,
    onBackClick: () -> Unit,
    onExperimentStarted: () -> Unit = {}
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasCameraPermission = isGranted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val simulationEngine = remember { SimulationEngine() }
    val renderer = remember { AR3DRenderer(context) }

    var isRunning by remember { mutableStateOf(false) }
    var isPanelExpanded by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var hasStartedOnce by remember { mutableStateOf(false) }
    var arModeEnabled by remember { mutableStateOf(hasCameraPermission) }
    val paramScope = rememberCoroutineScope()
    var paramResetJob by remember { mutableStateOf<Job?>(null) }

    var pendulumParams by remember { mutableStateOf(PendulumParameters()) }
    var freeFallParams by remember { mutableStateOf(FreeFallParameters()) }
    var collisionParams by remember { mutableStateOf(CollisionParameters()) }
    var circuitParams by remember { mutableStateOf(CircuitParameters()) }
    var magneticFieldParams by remember { mutableStateOf(MagneticFieldParameters()) }
    var refractionParams by remember { mutableStateOf(RefractionParameters()) }
    var lensParams by remember { mutableStateOf(LensParameters()) }
    var brownianParams by remember { mutableStateOf(BrownianMotionParameters()) }
    var gasExpansionParams by remember { mutableStateOf(GasExpansionParameters()) }

    var pendulumState by remember { mutableStateOf(PendulumState()) }
    var freeFallState by remember { mutableStateOf(FreeFallState()) }
    var collisionState by remember { mutableStateOf(CollisionState()) }
    var circuitState by remember { mutableStateOf(CircuitState()) }
    var magneticFieldState by remember { mutableStateOf(MagneticFieldState()) }
    var refractionState by remember { mutableStateOf(RefractionState()) }
    var lensState by remember { mutableStateOf(LensState()) }
    var brownianState by remember { mutableStateOf(BrownianMotionState()) }
    var gasExpansionState by remember { mutableStateOf(GasExpansionState()) }

    LaunchedEffect(isRunning, experimentType) {
        while (isRunning) {
            when (experimentType) {
                ExperimentType.PENDULUM         -> pendulumState = simulationEngine.updatePendulum(pendulumParams, pendulumState)
                ExperimentType.FREE_FALL        -> {
                    freeFallState = simulationEngine.updateFreeFall(freeFallParams, freeFallState)
                    if (freeFallState.hasLanded) isRunning = false
                }
                ExperimentType.COLLISION        -> collisionState = simulationEngine.updateCollision(collisionParams, collisionState)
                ExperimentType.ELECTRIC_CIRCUIT -> circuitState = simulationEngine.updateCircuit(circuitParams, circuitState)
                ExperimentType.MAGNETIC_FIELD   -> magneticFieldState = simulationEngine.updateMagneticField(magneticFieldParams, magneticFieldState)
                ExperimentType.LIGHT_REFRACTION -> refractionState = simulationEngine.updateRefraction(refractionParams, refractionState)
                ExperimentType.LENS             -> lensState = simulationEngine.updateLens(lensParams, lensState)
                ExperimentType.BROWNIAN_MOTION  -> brownianState = simulationEngine.updateBrownianMotion(brownianParams, brownianState)
                ExperimentType.GAS_EXPANSION    -> gasExpansionState = simulationEngine.updateGasExpansion(gasExpansionParams, gasExpansionState)
            }
            delay(16)
        }
    }

    fun startSimulation() {
        when (experimentType) {
            ExperimentType.PENDULUM         -> pendulumState = simulationEngine.initPendulum(pendulumParams)
            ExperimentType.FREE_FALL        -> freeFallState = simulationEngine.initFreeFall(freeFallParams)
            ExperimentType.COLLISION        -> collisionState = simulationEngine.initCollision(collisionParams)
            ExperimentType.ELECTRIC_CIRCUIT -> circuitState = simulationEngine.initCircuit(circuitParams)
            ExperimentType.MAGNETIC_FIELD   -> magneticFieldState = simulationEngine.initMagneticField(magneticFieldParams)
            ExperimentType.LIGHT_REFRACTION -> refractionState = simulationEngine.initRefraction(refractionParams)
            ExperimentType.LENS             -> lensState = simulationEngine.initLens(lensParams)
            ExperimentType.BROWNIAN_MOTION  -> brownianState = simulationEngine.initBrownianMotion(brownianParams)
            ExperimentType.GAS_EXPANSION    -> gasExpansionState = simulationEngine.initGasExpansion(gasExpansionParams)
        }
        if (!hasStartedOnce) {
            hasStartedOnce = true
            onExperimentStarted()
        }
        isRunning = true
    }

    fun resetSimulation() {
        isRunning = false
        startSimulation()
    }

    fun scheduleRestart() {
        if (hasStartedOnce) {
            paramResetJob?.cancel()
            paramResetJob = paramScope.launch {
                delay(400)
                startSimulation()
            }
        }
    }

    val anySimulationStarted = pendulumState.isRunning || freeFallState.isRunning ||
            collisionState.isRunning || circuitState.isRunning || magneticFieldState.isRunning ||
            refractionState.isRunning || lensState.isRunning || brownianState.isRunning ||
            gasExpansionState.isRunning

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Layer 0: Background (AR camera or themed gradient) ──────────────
        if (hasCameraPermission && arModeEnabled) {
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val childNodes = rememberNodes()
            ARScene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                childNodes = childNodes,
                planeRenderer = false,
                sessionConfiguration = { session, config ->
                    config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                onGestureListener = object : io.github.sceneview.gesture.GestureDetector.OnGestureListener {
                    override fun onDown(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onShowPress(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onSingleTapUp(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onSingleTapConfirmed(e: MotionEvent, node: io.github.sceneview.node.Node?) {
                        if (!anySimulationStarted) startSimulation()
                    }
                    override fun onDoubleTap(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onDoubleTapEvent(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onContextClick(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onLongPress(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onFling(e1: MotionEvent?, e2: MotionEvent, node: io.github.sceneview.node.Node?, velocity: dev.romainguy.kotlin.math.Float2) {}
                    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, node: io.github.sceneview.node.Node?, distance: dev.romainguy.kotlin.math.Float2) {}
                    override fun onMoveBegin(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onMove(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onMoveEnd(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onRotateBegin(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onRotate(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onRotateEnd(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onScaleBegin(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onScale(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onScaleEnd(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                }
            )
            // No overlay — camera feed is fully visible, simulation canvas renders on top
        } else {
            // Physics-themed gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B2D3E), Color(0xFF0A1628))
                        )
                    )
            )
        }

        // ── Layer 1: Simulation canvas — ALWAYS VISIBLE ─────────────────────
        SimulationCanvas(
            experimentType = experimentType,
            pendulumState = pendulumState,
            pendulumParams = pendulumParams,
            freeFallState = freeFallState,
            freeFallParams = freeFallParams,
            collisionState = collisionState,
            collisionParams = collisionParams,
            circuitState = circuitState,
            magneticFieldState = magneticFieldState,
            refractionState = refractionState,
            refractionParams = refractionParams,
            lensState = lensState,
            lensParams = lensParams,
            brownianState = brownianState,
            gasExpansionState = gasExpansionState,
            isARMode = hasCameraPermission && arModeEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.TopCenter)
                .padding(top = 64.dp, bottom = 8.dp)
                .then(
                    if (!anySimulationStarted)
                        Modifier.clickable { startSimulation() }
                    else
                        Modifier
                )
        )

        // ── Layer 2: Top bar ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = {
                    isRunning = false
                    onBackClick()
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = Color.White)
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Text(
                    text = experimentType.displayName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // AR indicator badge
                if (hasCameraPermission) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (arModeEnabled) Color(0xFF00C853).copy(alpha = 0.85f)
                                else Color.White.copy(alpha = 0.20f)
                    ) {
                        Text(
                            text = "AR",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // Camera toggle button
                if (hasCameraPermission) {
                    FilledIconButton(
                        onClick = { arModeEnabled = !arModeEnabled },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (arModeEnabled)
                                Color(0xFF00C853).copy(alpha = 0.25f)
                            else
                                Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Icon(
                            imageVector = if (arModeEnabled) Icons.Default.Videocam
                                          else Icons.Default.VideocamOff,
                            contentDescription = if (arModeEnabled) "Отключить камеру"
                                                 else "Включить камеру",
                            tint = Color.White
                        )
                    }
                }
                FilledIconButton(
                    onClick = { showInfo = !showInfo },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (showInfo)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        else
                            Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Icon(Icons.Default.Info, "Данные", tint = Color.White)
                }
            }
        }

        // ── Layer 3: Info panel (top right) ─────────────────────────────────
        AnimatedVisibility(
            visible = showInfo,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 76.dp, end = 12.dp),
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            ExperimentInfoPanel(
                experimentType = experimentType,
                pendulumState = pendulumState,
                freeFallState = freeFallState,
                collisionState = collisionState,
                circuitState = circuitState,
                magneticFieldState = magneticFieldState,
                refractionState = refractionState,
                lensState = lensState,
                brownianState = brownianState,
                gasExpansionState = gasExpansionState,
                pendulumParams = pendulumParams,
                freeFallParams = freeFallParams,
                collisionParams = collisionParams,
                circuitParams = circuitParams,
                refractionParams = refractionParams,
                lensParams = lensParams,
                modifier = Modifier.width(240.dp)
            )
        }

        // ── Layer 4: Start prompt (when simulation not yet started) ──────────
        if (!anySimulationStarted) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 140.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp).size(40.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "Нажмите на экран или\nкнопку ▶ для запуска",
                        color = Color.White.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ── Layer 5: Control buttons + parameters panel (bottom) ─────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Play/Pause + Reset row
            Row(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset
                IconButton(
                    onClick = { resetSimulation() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Default.Refresh, "Сброс", tint = Color.White)
                }

                // Play / Pause
                FilledIconButton(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                        } else {
                            if (!anySimulationStarted) startSimulation() else isRunning = true
                        }
                    },
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Пауза" else "Старт",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Parameters toggle
                IconButton(
                    onClick = { isPanelExpanded = !isPanelExpanded },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isPanelExpanded)
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            else
                                Color.White.copy(alpha = 0.12f)
                        )
                ) {
                    Icon(Icons.Default.Tune, "Параметры", tint = Color.White)
                }
            }

            // Parameters panel
            ParametersPanel(
                experimentType = experimentType,
                isExpanded = isPanelExpanded,
                onToggle = { isPanelExpanded = !isPanelExpanded },
                pendulumParams = pendulumParams,
                onPendulumParamsChange = { pendulumParams = it; scheduleRestart() },
                freeFallParams = freeFallParams,
                onFreeFallParamsChange = { freeFallParams = it; scheduleRestart() },
                collisionParams = collisionParams,
                onCollisionParamsChange = { collisionParams = it; scheduleRestart() },
                circuitParams = circuitParams,
                onCircuitParamsChange = { circuitParams = it; scheduleRestart() },
                magneticFieldParams = magneticFieldParams,
                onMagneticFieldParamsChange = { magneticFieldParams = it; scheduleRestart() },
                refractionParams = refractionParams,
                onRefractionParamsChange = { refractionParams = it; scheduleRestart() },
                lensParams = lensParams,
                onLensParamsChange = { lensParams = it; scheduleRestart() },
                brownianParams = brownianParams,
                onBrownianParamsChange = { brownianParams = it; scheduleRestart() },
                gasExpansionParams = gasExpansionParams,
                onGasExpansionParamsChange = { gasExpansionParams = it; scheduleRestart() }
            )
        }
    }

}
