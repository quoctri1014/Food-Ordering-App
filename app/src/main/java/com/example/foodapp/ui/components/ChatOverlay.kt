package com.example.foodapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat // Đã sửa icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.example.foodapp.ui.theme.PrimaryOrange

object ChatPositionStore {
    var x: Float = 0f
    var y: Float = 0f
    var isInitialized = false
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ChatOverlay(
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val buttonSizePx = with(density) { 56.dp.toPx() }

    val offsetX = remember { Animatable(ChatPositionStore.x) }
    val offsetY = remember { Animatable(ChatPositionStore.y) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!ChatPositionStore.isInitialized) {
            val defaultX = screenWidthPx - buttonSizePx - with(density) { 16.dp.toPx() }
            val defaultY = screenHeightPx - with(density) { 150.dp.toPx() }
            offsetX.snapTo(defaultX)
            offsetY.snapTo(defaultY)
            ChatPositionStore.x = defaultX
            ChatPositionStore.y = defaultY
            ChatPositionStore.isInitialized = true
        } else {
            offsetX.snapTo(ChatPositionStore.x)
            offsetY.snapTo(ChatPositionStore.y)
        }
    }

    DraggableChatButtonUI(
        offsetX = offsetX.value,
        offsetY = offsetY.value,
        onDrag = { dragX, dragY ->
            scope.launch {
                val newX = (offsetX.value + dragX).coerceIn(0f, screenWidthPx - buttonSizePx)
                val newY = (offsetY.value + dragY).coerceIn(0f, screenHeightPx - buttonSizePx)
                offsetX.snapTo(newX)
                offsetY.snapTo(newY)
                ChatPositionStore.x = newX
                ChatPositionStore.y = newY
            }
        },
        onDragEnd = {
            scope.launch {
                val centerScreen = (screenWidthPx - buttonSizePx) / 2
                val targetX = if (offsetX.value < centerScreen) 16f else screenWidthPx - buttonSizePx - 16f
                offsetX.animateTo(
                    targetValue = targetX,
                    animationSpec = tween(durationMillis = 300)
                )
                ChatPositionStore.x = targetX
                ChatPositionStore.y = offsetY.value
            }
        },
        onClick = onClick
    )
}

@Composable
private fun DraggableChatButtonUI(
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PrimaryOrange,
        contentColor = Color.White,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .size(56.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onDragEnd() }
                )
            }
    ) {
        Icon(Icons.Filled.Chat, contentDescription = "Chat")
    }
}