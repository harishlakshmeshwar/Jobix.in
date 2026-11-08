package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.*

private data class Point3D(val x: Float, val y: Float, val z: Float)

private data class ProjectedNode(
    val x2d: Float,
    val y2d: Float,
    val scale: Float,
    val alpha: Float,
    val label: String,
    val color: Color
)

@Composable
fun Interactive3DCareerCanvas(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    // Continuous orbital rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "3d_rotation")
    val autoAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auto_angle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Interactive drag angle offsets
    var userAngleX by remember { mutableFloatStateOf(0f) }
    var userAngleY by remember { mutableFloatStateOf(0f) }

    val rawNodes = remember {
        listOf(
            Triple(Point3D(-90f, -40f, 60f), "Android", JobixBlueLight),
            Triple(Point3D(90f, -60f, -40f), "Cloud AI", JobixCyan),
            Triple(Point3D(40f, 80f, 70f), "FinTech", JobixViolet),
            Triple(Point3D(-80f, 70f, -60f), "FullStack", JobixPurple),
            Triple(Point3D(120f, 10f, 30f), "Hackathons", JobixEmerald),
            Triple(Point3D(-110f, 10f, -20f), "Razorpay", JobixBlue),
            Triple(Point3D(0f, -100f, 50f), "Google", Color(0xFFEA4335)),
            Triple(Point3D(0f, 100f, -50f), "Startups", JobixAmber)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    userAngleY += dragAmount.x * 0.35f
                    userAngleX = (userAngleX - dragAmount.y * 0.35f).coerceIn(-45f, 45f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val fov = 350f

            val totalAngleY = Math.toRadians((autoAngle + userAngleY).toDouble()).toFloat()
            val totalAngleX = Math.toRadians(userAngleX.toDouble()).toFloat()

            // 1. Draw glowing 3D orbital rings
            draw3DOrbitalRings(
                centerX, centerY, totalAngleX, totalAngleY, pulseScale, isDark
            )

            // 2. Project 3D nodes
            val projected = rawNodes.map { (p, label, color) ->
                // Rotate around Y
                val cosY = cos(totalAngleY)
                val sinY = sin(totalAngleY)
                val x1 = p.x * cosY + p.z * sinY
                val z1 = -p.x * sinY + p.z * cosY

                // Rotate around X
                val cosX = cos(totalAngleX)
                val sinX = sin(totalAngleX)
                val y2 = p.y * cosX - z1 * sinX
                val z2 = p.y * sinX + z1 * cosX

                // Perspective projection
                val distance = fov / (fov + z2 + 160f)
                val projX = centerX + x1 * distance * pulseScale
                val projY = centerY + y2 * distance * pulseScale
                val alpha = (0.35f + ((z2 + 120f) / 240f) * 0.65f).coerceIn(0.25f, 1.0f)

                ProjectedNode(projX, projY, distance, alpha, label, color)
            }.sortedBy { it.scale }

            // 3. Draw interconnected Constellation Lines
            for (i in projected.indices) {
                val nodeA = projected[i]
                val nextNode = projected[(i + 1) % projected.size]
                val midNode = projected[(i + 3) % projected.size]

                val lineColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF2563EB)
                val lineAlpha = ((nodeA.alpha + nextNode.alpha) / 2f * 0.4f).coerceIn(0.1f, 0.5f)

                drawLine(
                    color = lineColor.copy(alpha = lineAlpha),
                    start = Offset(nodeA.x2d, nodeA.y2d),
                    end = Offset(nextNode.x2d, nextNode.y2d),
                    strokeWidth = 1.5f * nodeA.scale
                )

                drawLine(
                    color = JobixViolet.copy(alpha = (lineAlpha * 0.5f).coerceIn(0.05f, 0.3f)),
                    start = Offset(nodeA.x2d, nodeA.y2d),
                    end = Offset(midNode.x2d, midNode.y2d),
                    strokeWidth = 1.0f * nodeA.scale
                )
            }

            // 4. Draw Center Jobix Core Nucleus
            val corePulse = 22f * pulseScale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JobixCyan.copy(alpha = 0.85f),
                        JobixBlue.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = corePulse * 2.2f
                ),
                radius = corePulse * 2.2f,
                center = Offset(centerX, centerY)
            )

            drawCircle(
                color = if (isDark) Color.White else JobixNavyDark,
                radius = corePulse * 0.6f,
                center = Offset(centerX, centerY)
            )

            // 5. Draw Orbiting 3D Nodes
            for (node in projected) {
                val nodeRadius = (9f * node.scale * pulseScale).coerceIn(4f, 16f)

                // Outer Halo
                drawCircle(
                    color = node.color.copy(alpha = node.alpha * 0.35f),
                    radius = nodeRadius * 2.4f,
                    center = Offset(node.x2d, node.y2d)
                )

                // Core Sphere
                drawCircle(
                    color = node.color.copy(alpha = node.alpha),
                    radius = nodeRadius,
                    center = Offset(node.x2d, node.y2d)
                )

                // White specular reflection highlight
                drawCircle(
                    color = Color.White.copy(alpha = node.alpha * 0.8f),
                    radius = nodeRadius * 0.35f,
                    center = Offset(node.x2d - nodeRadius * 0.3f, node.y2d - nodeRadius * 0.3f)
                )
            }
        }
    }
}

private fun DrawScope.draw3DOrbitalRings(
    centerX: Float,
    centerY: Float,
    angleX: Float,
    angleY: Float,
    pulse: Float,
    isDark: Boolean
) {
    val ringColor1 = if (isDark) Color(0xFF00F5D4) else Color(0xFF2563EB)
    val ringColor2 = if (isDark) Color(0xFF8B5CF6) else Color(0xFF7C3AED)

    // Inner inclined orbit
    drawOval(
        brush = Brush.sweepGradient(
            listOf(
                ringColor1.copy(alpha = 0.5f),
                ringColor2.copy(alpha = 0.2f),
                ringColor1.copy(alpha = 0.5f)
            )
        ),
        topLeft = Offset(centerX - 130f * pulse, centerY - 55f * pulse),
        size = androidx.compose.ui.geometry.Size(260f * pulse, 110f * pulse),
        style = Stroke(width = 1.8f)
    )

    // Outer cross inclined orbit
    drawOval(
        brush = Brush.sweepGradient(
            listOf(
                ringColor2.copy(alpha = 0.45f),
                ringColor1.copy(alpha = 0.15f),
                ringColor2.copy(alpha = 0.45f)
            )
        ),
        topLeft = Offset(centerX - 170f * pulse, centerY - 75f * pulse),
        size = androidx.compose.ui.geometry.Size(340f * pulse, 150f * pulse),
        style = Stroke(width = 1.4f)
    )
}
