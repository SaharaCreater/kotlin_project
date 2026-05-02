package com.app.dopp.ui_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.model.GLTFLoader.loadModelAsync

@Composable
fun ARScreen(modelUrl: String) {
    val context = LocalContext.current
    // Получаем жизненный цикл для асинхронной загрузки модели
    val coroutineScope = LocalLifecycleOwner.current.lifecycleScope

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            planeRenderer = true,
            onCreate = { arSceneView ->
                // Настраиваем слушатель нажатий на плоскость
                arSceneView.onTapAr = { hitResult, _ ->
                    // Создаем узел модели, используя движок сцены
                    val modelNode = ArModelNode(arSceneView.engine).apply {
                        // Используем сигнатуру из твоего скриншота «Снимок экрана — 2026-05-02 в 02.35.11.png»
                        loadModelAsync(
                            context = context,
                            gltfFileLocation = modelUrl,
                            coroutineScope = coroutineScope,
                            result = { model ->
                                // Когда модель загружена, задаем ей размер
                                model?.let {
                                    // Масштаб 0.5f (50см), если модель слишком большая
                                    this.scale = Position(0.5f, 0.5f, 0.5f)
                                }
                            }
                        )
                    }
                    // Добавляем узел на сцену и привязываем к точке "тапа"
                    arSceneView.addChild(modelNode)
                    modelNode.anchor = hitResult.createAnchor()
                }
            }
        )

        // Инструкция для пользователя поверх AR-сцены
        Text(
            text = "Наведите камеру на ровную поверхность и нажмите, чтобы увидеть модель",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
        )
    }
}