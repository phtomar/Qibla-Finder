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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CompassReading
import com.example.model.QiblaInfo
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.LocalAppTheme
import com.example.util.QiblaMath
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaCompassDial(
    compass: CompassReading,
    qibla: QiblaInfo,
    useTrueNorth: Boolean,
    declination: Float,
    dialRotationMode: Boolean,
    modifier: Modifier = Modifier
) {
    val theme = LocalAppTheme.current

    // Calculate the actual effective heading (True Heading or Magnetic Heading)
    val effectiveHeading = if (useTrueNorth) {
        QiblaMath.normalize360(compass.azimuth + declination)
    } else {
        compass.azimuth
    }

    // Unwrapped continuous angle tracking to eliminate 0/360 wrap-around spins
    var continuousHeading by remember { mutableFloatStateOf(effectiveHeading) }
    var lastRawHeading by remember { mutableFloatStateOf(effectiveHeading) }

    LaunchedEffect(effectiveHeading) {
        var diff = effectiveHeading - lastRawHeading
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        continuousHeading += diff
        lastRawHeading = effectiveHeading
    }

    var continuousQiblaBearing by remember { mutableFloatStateOf(qibla.qiblaBearing.toFloat()) }
    var lastRawBearing by remember { mutableFloatStateOf(qibla.qiblaBearing.toFloat()) }

    LaunchedEffect(qibla.qiblaBearing) {
        val raw = qibla.qiblaBearing.toFloat()
        var diff = raw - lastRawBearing
        while (diff > 180f) diff -= 360f
        while (diff < -180f) diff += 360f
        continuousQiblaBearing += diff
        lastRawBearing = raw
    }

    // Smooth spring animations without jitter or sudden spins
    val animatedHeading by animateFloatAsState(
        targetValue = continuousHeading,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 420f),
        label = "heading_anim"
    )

    val animatedQiblaBearing by animateFloatAsState(
        targetValue = continuousQiblaBearing,
        animationSpec = spring(dampingRatio = 0.88f, stiffness = 380f),
        label = "qibla_bearing_anim"
    )

    // Relative angle for the guidance needle
    val relativeAngle = animatedQiblaBearing - animatedHeading

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

    val headingCardinal = QiblaMath.getCardinalDirection(effectiveHeading.toDouble())

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // OUTSIDE OF DIAL: Clean Device Heading Pill
        Box(
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(theme.surfaceVariant)
                .border(1.dp, if (qibla.isAligned) theme.primary.copy(alpha = 0.6f) else theme.borderSubtle, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("dial_outside_heading")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = if (qibla.isAligned) theme.primary else theme.secondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = String.format(Locale.getDefault(), "%d° %s", effectiveHeading.roundToInt(), headingCardinal),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (qibla.isAligned) theme.primary else theme.textPrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Compass Dial Container
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(8.dp)
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
                    // ROTATING DIAL MODE:
                    // 1. Compass Rose (ticks, N, E, S, W) rotates with -animatedHeading.
                    // 2. Kaaba Icon is fixed at the TOP of the dial (12 o'clock / 0°).
                    // 3. Central Pointer Needle rotates dynamically with relative angle:
                    //    (animatedQiblaBearing - animatedHeading).
                    //    When facing the Qibla, relative angle = 0°, so needle points straight UP
                    //    into the Kaaba icon at the top!
                    // =========================================================================
                    // Rotating Rose
                    rotate(-animatedHeading, pivot = center) {
                        drawDegreeTicks(center, ringRadius, theme)
                        drawGeometricCardinals(center, ringRadius, theme)
                    }

                    // Guidance needle rotating towards the relative angle
                    rotate(relativeAngle, pivot = center) {
                        drawPointerNeedle(center, ringRadius, qibla.isAligned, theme)
                    }

                    // Top indicator drawn first in background
                    drawTopIndicator(center, ringRadius, qibla.isAligned, theme)

                    // Kaaba Marker Badge drawn on top covering the triangle tip
                    drawKaabaMarkerBadge(
                        center = center,
                        ringRadius = ringRadius,
                        bearingDeg = 0f,
                        isAligned = qibla.isAligned,
                        pulseGlow = pulseGlow,
                        theme = theme
                    )
                } else {
                    // =========================================================================
                    // FIXED DIAL MODE:
                    // 1. Compass Rose is FIXED with North at 12 o'clock.
                    // 2. Kaaba is fixed at the TOP (12 o'clock / 0°).
                    // 3. Central Pointer Needle moves dynamically:
                    //    Points directly in the direction of the Kaaba (relativeAngle = animatedQiblaBearing - animatedHeading)
                    //    So when aligned with the Qibla, needle points straight UP to the Kaaba at the top!
                    // =========================================================================
                    // Fixed Compass Rose
                    drawDegreeTicks(center, ringRadius, theme)
                    drawGeometricCardinals(center, ringRadius, theme)

                    // Guidance Needle rotating dynamically to point to the Kaaba
                    rotate(relativeAngle, pivot = center) {
                        drawPointerNeedle(center, ringRadius, qibla.isAligned, theme)
                    }

                    // Fixed Top Reference indicator drawn first
                    drawTopIndicator(center, ringRadius, qibla.isAligned, theme)

                    // Kaaba Marker at top (0°) covering the indicator
                    drawKaabaMarkerBadge(
                        center = center,
                        ringRadius = ringRadius,
                        bearingDeg = 0f,
                        isAligned = qibla.isAligned,
                        pulseGlow = pulseGlow,
                        theme = theme
                    )
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

    // Connector ring to dial
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
 * Draws the guidance pointer needle aiming directly towards the Kaaba marker badge.
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

    // Tapered Arrowhead Path pointing directly forward
    val arrowPath = Path().apply {
        moveTo(center.x, needleTipY)
        lineTo(center.x - needleBaseWidth, center.y - needleLen * 0.65f)
        lineTo(center.x - needleBaseWidth * 0.4f, center.y - 12.dp.toPx())
        lineTo(center.x + needleBaseWidth * 0.4f, center.y - 12.dp.toPx())
        lineTo(center.x + needleBaseWidth, center.y - needleLen * 0.65f)
        close()
    }

    // Shadow / glow behind arrow when aligned
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

    // Needle border
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

private fun DrawScope.drawGeometricCenterHub(
    center: Offset,
    pitch: Float,
    roll: Float,
    isLevel: Boolean,
    isAligned: Boolean,
    theme: AppThemePalette
) {
    val hubRadius = 18.dp.toPx()

    // Outer rim
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
