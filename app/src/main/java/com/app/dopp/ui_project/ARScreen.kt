package com.app.dopp.ui_project

import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    experimentType: ExperimentType,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == 
                PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // Simulation engine
    val simulationEngine = remember { SimulationEngine() }
    val renderer = remember { AR3DRenderer(context) }
    
    // Simulation state
    var isRunning by remember { mutableStateOf(false) }
    var isPanelExpanded by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(true) }
    
    // Parameters state
    var pendulumParams by remember { mutableStateOf(PendulumParameters()) }
    var freeFallParams by remember { mutableStateOf(FreeFallParameters()) }
    var collisionParams by remember { mutableStateOf(CollisionParameters()) }
    var circuitParams by remember { mutableStateOf(CircuitParameters()) }
    var magneticFieldParams by remember { mutableStateOf(MagneticFieldParameters()) }
    var refractionParams by remember { mutableStateOf(RefractionParameters()) }
    var lensParams by remember { mutableStateOf(LensParameters()) }
    var brownianParams by remember { mutableStateOf(BrownianMotionParameters()) }
    var gasExpansionParams by remember { mutableStateOf(GasExpansionParameters()) }
    
    // Simulation state
    var pendulumState by remember { mutableStateOf(PendulumState()) }
    var freeFallState by remember { mutableStateOf(FreeFallState()) }
    var collisionState by remember { mutableStateOf(CollisionState()) }
    var circuitState by remember { mutableStateOf(CircuitState()) }
    var magneticFieldState by remember { mutableStateOf(MagneticFieldState()) }
    var refractionState by remember { mutableStateOf(RefractionState()) }
    var lensState by remember { mutableStateOf(LensState()) }
    var brownianState by remember { mutableStateOf(BrownianMotionState()) }
    var gasExpansionState by remember { mutableStateOf(GasExpansionState()) }
    
    // Simulation loop
    LaunchedEffect(isRunning, experimentType) {
        while (isRunning) {
            when (experimentType) {
                ExperimentType.PENDULUM -> {
                    pendulumState = simulationEngine.updatePendulum(pendulumParams, pendulumState)
                }
                ExperimentType.FREE_FALL -> {
                    freeFallState = simulationEngine.updateFreeFall(freeFallParams, freeFallState)
                    if (freeFallState.hasLanded) {
                        isRunning = false
                    }
                }
                ExperimentType.COLLISION -> {
                    collisionState = simulationEngine.updateCollision(collisionParams, collisionState)
                }
                ExperimentType.ELECTRIC_CIRCUIT -> {
                    circuitState = simulationEngine.updateCircuit(circuitParams, circuitState)
                }
                ExperimentType.MAGNETIC_FIELD -> {
                    magneticFieldState = simulationEngine.updateMagneticField(magneticFieldParams, magneticFieldState)
                }
                ExperimentType.LIGHT_REFRACTION -> {
                    refractionState = simulationEngine.updateRefraction(refractionParams, refractionState)
                }
                ExperimentType.LENS -> {
                    lensState = simulationEngine.updateLens(lensParams, lensState)
                }
                ExperimentType.BROWNIAN_MOTION -> {
                    brownianState = simulationEngine.updateBrownianMotion(brownianParams, brownianState)
                }
                ExperimentType.GAS_EXPANSION -> {
                    gasExpansionState = simulationEngine.updateGasExpansion(gasExpansionParams, gasExpansionState)
                }
            }
            delay(16) // ~60 FPS
        }
    }
    
    // Start/Reset simulation
    fun startSimulation() {
        when (experimentType) {
            ExperimentType.PENDULUM -> {
                pendulumState = simulationEngine.initPendulum(pendulumParams)
            }
            ExperimentType.FREE_FALL -> {
                freeFallState = simulationEngine.initFreeFall(freeFallParams)
            }
            ExperimentType.COLLISION -> {
                collisionState = simulationEngine.initCollision(collisionParams)
            }
            ExperimentType.ELECTRIC_CIRCUIT -> {
                circuitState = simulationEngine.initCircuit(circuitParams)
            }
            ExperimentType.MAGNETIC_FIELD -> {
                magneticFieldState = simulationEngine.initMagneticField(magneticFieldParams)
            }
            ExperimentType.LIGHT_REFRACTION -> {
                refractionState = simulationEngine.initRefraction(refractionParams)
            }
            ExperimentType.LENS -> {
                lensState = simulationEngine.initLens(lensParams)
            }
            ExperimentType.BROWNIAN_MOTION -> {
                brownianState = simulationEngine.initBrownianMotion(brownianParams)
            }
            ExperimentType.GAS_EXPANSION -> {
                gasExpansionState = simulationEngine.initGasExpansion(gasExpansionParams)
            }
        }
        isRunning = true
    }
    
    fun resetSimulation() {
        isRunning = false
        startSimulation()
    }
    
    // Check if any simulation is running
    val anySimulationStarted = pendulumState.isRunning || 
        freeFallState.isRunning || 
        collisionState.isRunning ||
        circuitState.isRunning ||
        magneticFieldState.isRunning ||
        refractionState.isRunning ||
        lensState.isRunning ||
        brownianState.isRunning ||
        gasExpansionState.isRunning

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            // AR Scene
            val engine = rememberEngine()
            val modelLoader = rememberModelLoader(engine)
            val childNodes = rememberNodes()
            
            ARScene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                childNodes = childNodes,
                planeRenderer = true,
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
                    override fun onSingleTapUp(e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onSingleTapConfirmed(e: MotionEvent, node: io.github.sceneview.node.Node?) {
                        if (!anySimulationStarted) {
                            startSimulation()
                        }
                    }
                    override fun onDoubleTap(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onDoubleTapEvent(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onContextClick(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onLongPress(e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onFling(e1: MotionEvent?, e2: MotionEvent, node: io.github.sceneview.node.Node?, velocity: dev.romainguy.kotlin.math.Float2): Boolean = false
                    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, node: io.github.sceneview.node.Node?, distance: dev.romainguy.kotlin.math.Float2): Boolean = false
                    override fun onMoveBegin(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onMove(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onMoveEnd(detector: io.github.sceneview.gesture.MoveGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onRotateBegin(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onRotate(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onRotateEnd(detector: io.github.sceneview.gesture.RotateGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                    override fun onScaleBegin(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onScale(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?): Boolean = false
                    override fun onScaleEnd(detector: io.github.sceneview.gesture.ScaleGestureDetector, e: MotionEvent, node: io.github.sceneview.node.Node?) {}
                }
            )
            
            // Simulation Canvas Overlay (2D representation when 3D is not available)
            SimulationCanvasOverlay(
                experimentType = experimentType,
                isRunning = isRunning,
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
                gasExpansionState = gasExpansionState
            )
        } else {
            // No permission screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Требуется доступ к камере",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Для работы AR необходимо разрешение на использование камеры",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Предоставить доступ")
                    }
                }
            }
        }
        
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            FilledIconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            
            // Experiment title
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Text(
                    text = experimentType.displayName,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Info toggle
            FilledIconButton(
                onClick = { showInfo = !showInfo },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (showInfo) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Информация"
                )
            }
        }
        
        // Info panel (top right)
        AnimatedVisibility(
            visible = showInfo && anySimulationStarted,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp),
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
                modifier = Modifier.width(280.dp)
            )
        }
        
        // Instruction text (when not started)
        if (!anySimulationStarted && hasCameraPermission) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Нажмите на экран или кнопку воспроизведения",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "чтобы запустить эксперимент",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Control buttons (bottom center)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Play/Pause button
            FilledIconButton(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                    } else {
                        if (!anySimulationStarted) {
                            startSimulation()
                        } else {
                            isRunning = true
                        }
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Пауза" else "Старт"
                )
            }
            
            // Reset button
            FilledIconButton(
                onClick = { resetSimulation() },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Сброс",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        // Parameters panel (bottom)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ParametersPanel(
                experimentType = experimentType,
                isExpanded = isPanelExpanded,
                onToggle = { isPanelExpanded = !isPanelExpanded },
                pendulumParams = pendulumParams,
                onPendulumParamsChange = { pendulumParams = it },
                freeFallParams = freeFallParams,
                onFreeFallParamsChange = { freeFallParams = it },
                collisionParams = collisionParams,
                onCollisionParamsChange = { collisionParams = it },
                circuitParams = circuitParams,
                onCircuitParamsChange = { circuitParams = it },
                magneticFieldParams = magneticFieldParams,
                onMagneticFieldParamsChange = { magneticFieldParams = it },
                refractionParams = refractionParams,
                onRefractionParamsChange = { refractionParams = it },
                lensParams = lensParams,
                onLensParamsChange = { lensParams = it },
                brownianParams = brownianParams,
                onBrownianParamsChange = { brownianParams = it },
                gasExpansionParams = gasExpansionParams,
                onGasExpansionParamsChange = { gasExpansionParams = it }
            )
        }
    }
}

@Composable
private fun SimulationCanvasOverlay(
    experimentType: ExperimentType,
    isRunning: Boolean,
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
    gasExpansionState: GasExpansionState
) {
    // This composable can render 2D Canvas overlays for the simulations
    // The actual 3D rendering happens in the AR Scene with the NodeData from AR3DRenderer
    
    // For now, we show a status indicator
    if (isRunning) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 80.dp)
        ) {
            // Status indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                    Text(
                        text = "Симуляция активна",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
