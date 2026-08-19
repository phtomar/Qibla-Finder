package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.CompassReading
import com.example.model.QiblaInfo
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.LocalAppTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun QiblaCompassDial(
    compass: CompassReading,
    qibla: QiblaInfo,
    dialRotationMode: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    // Pulse animation when aligned with Kaaba
    val infiniteTransition = rememberInfiniteTransition(label = "geo_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Animated dial rotation (Compass heading)
    val animatedAzimuth by animateFloatAsState(
        targetValue = compass.azimuth,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "azimuth_anim"
    )

    val animatedQiblaBearing by animateFloatAsState(
        targetValue = qibla.qiblaBearing.toFloat(),
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 380f),
        label = "qibla_bearing_anim"
    )

    val animatedRelativeAngle by animateFloatAsState(
        targetValue = qibla.relativeAngle,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 400f),
        label = "relative_angle_anim"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(12.dp)
            .shadow(
                14.dp,
                CircleShape,
                spotColor = if (qibla.isAligned) theme.primary.copy(alpha = 0.5f) else theme.border.copy(alpha = 0.3f)
            )
            .clip(CircleShape)
            .background(theme.surface)
            .border(12.dp, theme.surfaceVariant, CircleShape)
            .testTag("qibla_compass_dial"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f - 4.dp.toPx()
            val ringRadius = radius * 0.85f

            // 1. Outer dashed guide ring
            drawDashedGuideRing(center, radius * 0.95f, theme)

            // 2. Main inner ring
            drawCircle(
                color = if (qibla.isAligned) theme.primary.copy(alpha = 0.6f) else theme.border,
                radius = ringRadius,
                center = center,
                style = Stroke(width = if (qibla.isAligned) 2.dp.toPx() else 1.2.dp.toPx())
            )

            // 3. Alignment ambient radiance
            if (qibla.isAligned) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            theme.containerHigh.copy(alpha = 0.55f * pulseGlow),
                            theme.container.copy(alpha = 0.2f * pulseGlow),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }

            if (dialRotationMode) {
                // =========================================================================
                // ROTATING DIAL MODE (Compass Rose rotates with heading, Kaaba is on rose)
                // =========================================================================
                rotate(-animatedAzimuth, pivot = center) {
                    // Rose ticks and N, E, S, W
                    drawDegreeTicks(center, ringRadius, theme)
                    drawGeometricCardinals(center, ringRadius, theme)

                    // Destination: Kaaba Marker fixed on the compass ring at its geographical azimuth
                    drawKaabaMarkerBadge(
                        center = center,
                        ringRadius = ringRadius,
                        bearingDeg = animatedQiblaBearing,
                        isAligned = qibla.isAligned,
                        pulseGlow = pulseGlow,
                        theme = theme
                    )
                }

                // Top fixed heading indicator (phone forward direction)
                drawTopIndicator(center, ringRadius, qibla.isAligned, theme)

                // Independent Guidance Needle pointing from center towards the Kaaba
                rotate(animatedRelativeAngle, pivot = center) {
                    drawPointerNeedle(center, ringRadius, qibla.isAligned, theme)
                }
            } else {
                // =========================================================================
                // FIXED DIAL MODE (Option 1: North fixed at top, Kaaba on ring, needle rotates)
                // =========================================================================
                // 1. Compass Rose (Fixed: N at 12 o'clock, E at 3 o'clock, etc.)
                drawDegreeTicks(center, ringRadius, theme)
                drawGeometricCardinals(center, ringRadius, theme)

                // 2. Kaaba Marker on the ring at its exact geographic azimuth (e.g. 135° SE)
                drawKaabaMarkerBadge(
                    center = center,
                    ringRadius = ringRadius,
                    bearingDeg = animatedQiblaBearing,
                    isAligned = qibla.isAligned,
                    pulseGlow = pulseGlow,
                    theme = theme
                )

                // 3. Magnetic North Pointer Needle (Shows where true/magnetic North is relative to phone)
                rotate(-animatedAzimuth, pivot = center) {
                    drawNorthIndicator(center, ringRadius, theme)
                }

                // 4. Independent Qibla Pointer Needle (Sweeps dynamically toward the Kaaba marker on the ring)
                // When phone faces Kaaba, relativeAngle == 0, pointer points straight up into the aligned state!
                rotate(animatedRelativeAngle, pivot = center) {
                    drawPointerNeedle(center, ringRadius, qibla.isAligned, theme)
                }
            }

            // 4. Central Hub with Inclinometer / Spirit Level Bubble
            drawGeometricCenterHub(
                center = center,
                pitch = compass.pitch,
                roll = compass.roll,
                isLevel = compass.isLevel,
                isAligned = qibla.isAligned,
                theme = theme
            )
        }
    }
}

private fun DrawScope.drawDashedGuideRing(center: Offset, radius: Float, theme: AppThemePalette) {
    drawCircle(
        color = theme.dashedGuide.copy(alpha = 0.35f),
        radius = radius,
        center = center,
        style = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
        )
    )
}

private fun DrawScope.drawDegreeTicks(center: Offset, ringRadius: Float, theme: AppThemePalette) {
    val tickMajorLen = 10.dp.toPx()
    val tickMediumLen = 6.dp.toPx()
    val tickMinorLen = 3.dp.toPx()

    for (deg in 0 until 360 step 5) {
        val angleRad = Math.toRadians(deg.toDouble() - 90.0)
        val isMajor = deg % 30 == 0
        val isMedium = deg % 15 == 0 && !isMajor

        val tickLen = when {
            isMajor -> tickMajorLen
            isMedium -> tickMediumLen
            else -> tickMinorLen
        }

        val startX = center.x + (ringRadius - tickLen) * cos(angleRad).toFloat()
        val startY = center.y + (ringRadius - tickLen) * sin(angleRad).toFloat()
        val endX = center.x + ringRadius * cos(angleRad).toFloat()
        val endY = center.y + ringRadius * sin(angleRad).toFloat()

        val color = when {
            deg == 0 -> theme.north
            isMajor -> theme.secondary
            else -> theme.border.copy(alpha = 0.6f)
        }

        val strokeWidth = when {
            isMajor -> 1.8.dp.toPx()
            isMedium -> 1.2.dp.toPx()
            else -> 0.8.dp.toPx()
        }

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawGeometricCardinals(center: Offset, ringRadius: Float, theme: AppThemePalette) {
    val labelRadius = ringRadius - 20.dp.toPx()

    val northPaint = Paint().apply {
        color = theme.north.toArgb()
        textSize = 14.dp.toPx()
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    val cardinalPaint = Paint().apply {
        color = theme.cardinal.toArgb()
        textSize = 12.dp.toPx()
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }

    // N
    drawContext.canvas.nativeCanvas.drawText(
        "N",
        center.x,
        center.y - labelRadius + 4.dp.toPx(),
        northPaint
    )

    // E
    drawContext.canvas.nativeCanvas.drawText(
        "E",
        center.x + labelRadius,
        center.y + 4.dp.toPx(),
        cardinalPaint
    )

    // S
    drawContext.canvas.nativeCanvas.drawText(
        "S",
        center.x,
        center.y + labelRadius + 5.dp.toPx(),
        cardinalPaint
    )

    // W
    drawContext.canvas.nativeCanvas.drawText(
        "W",
        center.x - labelRadius,
        center.y + 4.dp.toPx(),
        cardinalPaint
    )
}

/**
 * Draws the Kaaba Marker Destination Badge anchored at its designated azimuth on the ring.
 */
private fun DrawScope.drawKaabaMarkerBadge(
    center: Offset,
    ringRadius: Float,
    bearingDeg: Float,
    isAligned: Boolean,
    pulseGlow: Float,
    theme: AppThemePalette
) {
    val angleRad = Math.toRadians(bearingDeg.toDouble() - 90.0)
    val markerX = center.x + ringRadius * cos(angleRad).toFloat()
    val markerY = center.y + ringRadius * sin(angleRad).toFloat()
    val markerCenter = Offset(markerX, markerY)

    val badgeRadius = 15.dp.toPx()

    // Outer radiant pulse when aligned
    if (isAligned) {
        drawCircle(
            color = theme.primary.copy(alpha = 0.45f * pulseGlow),
            radius = badgeRadius * 1.7f,
            center = markerCenter
        )
    }

    // Subtle connector dot to outer dial
    drawCircle(
        color = if (isAligned) theme.primary else theme.border,
        radius = badgeRadius + 2.dp.toPx(),
        center = markerCenter,
        style = Stroke(width = 1.dp.toPx())
    )

    // Primary badge circle
    drawCircle(
        color = if (isAligned) theme.kaabaBadge else (if (theme.isDark) Color(0xFF1E2620) else Color(0xFF263228)),
        radius = badgeRadius,
        center = markerCenter
    )
    drawCircle(
        color = if (isAligned) theme.goldAccent else Color.White.copy(alpha = 0.85f),
        radius = badgeRadius,
        center = markerCenter,
        style = Stroke(width = 1.8.dp.toPx())
    )

    // Miniature Kaaba icon inside badge
    val iconSize = 11.dp.toPx()
    val halfSize = iconSize / 2f
    val kaabaTopLeft = Offset(markerX - halfSize, markerY - halfSize)

    // Kaaba Black Cube base
    drawRect(
        color = Color(0xFF121413),
        topLeft = kaabaTopLeft,
        size = Size(iconSize, iconSize)
    )
    drawRect(
        color = if (isAligned) theme.goldAccent else Color.White,
        topLeft = kaabaTopLeft,
        size = Size(iconSize, iconSize),
        style = Stroke(width = 1.dp.toPx())
    )

    // Kiswa Gold Band across the top
    val bandY = markerY - halfSize + iconSize * 0.3f
    drawLine(
        color = theme.goldAccent,
        start = Offset(markerX - halfSize, bandY),
        end = Offset(markerX + halfSize, bandY),
        strokeWidth = 1.8.dp.toPx()
    )

    // Kaaba Door (Bab al-Kaaba)
    val doorWidth = iconSize * 0.22f
    val doorHeight = iconSize * 0.42f
    drawRect(
        color = theme.goldAccent,
        topLeft = Offset(markerX + halfSize * 0.15f, markerY + halfSize - doorHeight),
        size = Size(doorWidth, doorHeight)
    )
}

private fun DrawScope.drawTopIndicator(
    center: Offset,
    ringRadius: Float,
    isAligned: Boolean,
    theme: AppThemePalette
) {
    val pointerTop = center.y - ringRadius - 8.dp.toPx()
    val pointerBottom = center.y - ringRadius + 4.dp.toPx()
    val halfWidth = 5.dp.toPx()

    val path = Path().apply {
        moveTo(center.x, pointerBottom)
        lineTo(center.x - halfWidth, pointerTop)
        lineTo(center.x + halfWidth, pointerTop)
        close()
    }

    drawPath(
        path = path,
        color = if (isAligned) theme.primary else theme.secondary,
        style = Fill
    )
}

/**
 * Draws the independent pointing needle/arm that guides the user towards the Kaaba.
 */
private fun DrawScope.drawPointerNeedle(
    center: Offset,
    ringRadius: Float,
    isAligned: Boolean,
    theme: AppThemePalette
) {
    val needleLen = ringRadius - 16.dp.toPx()
    val needleBaseWidth = 6.dp.toPx()
    val needleTipY = center.y - needleLen

    // Tapered Arrowhead Path
    val arrowPath = Path().apply {
        moveTo(center.x, needleTipY)
        lineTo(center.x - needleBaseWidth, center.y - needleLen * 0.65f)
        lineTo(center.x - needleBaseWidth * 0.4f, center.y - 12.dp.toPx())
        lineTo(center.x + needleBaseWidth * 0.4f, center.y - 12.dp.toPx())
        lineTo(center.x + needleBaseWidth, center.y - needleLen * 0.65f)
        close()
    }

    // Shadow / glow behind arrow
    if (isAligned) {
        drawPath(
            path = arrowPath,
            color = theme.goldAccent.copy(alpha = 0.4f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }

    // Arrow fill with gradient
    drawPath(
        path = arrowPath,
        brush = Brush.verticalGradient(
            colors = if (isAligned) {
                listOf(theme.primary, theme.goldAccent)
            } else {
                listOf(theme.primary, theme.primary.copy(alpha = 0.5f))
            },
            startY = needleTipY,
            endY = center.y
        ),
        style = Fill
    )

    // Needle crisp border
    drawPath(
        path = arrowPath,
        color = if (isAligned) Color.White else theme.borderSubtle,
        style = Stroke(width = 1.dp.toPx())
    )

    // Central spine line
    drawLine(
        color = if (isAligned) theme.goldAccent else Color.White.copy(alpha = 0.7f),
        start = Offset(center.x, needleTipY + 2.dp.toPx()),
        end = Offset(center.x, center.y - 12.dp.toPx()),
        strokeWidth = 1.2.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Draws the North needle pointing towards magnetic / true north.
 */
private fun DrawScope.drawNorthIndicator(center: Offset, ringRadius: Float, theme: AppThemePalette) {
    val needleLen = ringRadius * 0.55f
    val halfWidth = 3.5.dp.toPx()

    val path = Path().apply {
        moveTo(center.x, center.y - needleLen)
        lineTo(center.x - halfWidth, center.y - 14.dp.toPx())
        lineTo(center.x + halfWidth, center.y - 14.dp.toPx())
        close()
    }

    drawPath(
        path = path,
        color = theme.north.copy(alpha = 0.85f),
        style = Fill
    )
}

private fun DrawScope.drawGeometricCenterHub(
    center: Offset,
    pitch: Float,
    roll: Float,
    isLevel: Boolean,
    isAligned: Boolean,
    theme: AppThemePalette
) {
    val hubRadius = 18.dp.toPx()

    // Outer white rim
    drawCircle(
        color = if (theme.isDark) theme.surfaceVariant else Color.White,
        radius = hubRadius,
        center = center
    )
    drawCircle(
        color = if (isAligned) theme.primary else theme.border,
        radius = hubRadius,
        center = center,
        style = Stroke(width = if (isAligned) 1.5.dp.toPx() else 1.dp.toPx())
    )

    // Inner center hub
    drawCircle(
        color = theme.centerHub,
        radius = hubRadius - 2.dp.toPx(),
        center = center
    )

    // Inclinometer Spirit Level Bubble inside hub
    val maxTilt = hubRadius - 6.dp.toPx()
    val bubbleOffsetX = (roll / 30f).coerceIn(-1f, 1f) * maxTilt
    val bubbleOffsetY = (-pitch / 30f).coerceIn(-1f, 1f) * maxTilt
    val bubbleCenter = Offset(center.x + bubbleOffsetX, center.y + bubbleOffsetY)

    drawCircle(
        color = if (isLevel) (if (isAligned) theme.goldAccent else Color.White) else theme.secondary,
        radius = 4.dp.toPx(),
        center = bubbleCenter
    )
}
