package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SensorAccuracy
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoError
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSuccess
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary
import com.example.ui.theme.GeoWarning
import com.example.ui.theme.LocalAppTheme
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationModal(
    accuracy: SensorAccuracy,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Animated position along Figure-8
    val infiniteTransition = rememberInfiniteTransition(label = "calib_motion")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "t"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GeoBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(GeoBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .testTag("calibration_modal"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStr.calibrationTitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal,
                        color = GeoTextPrimary
                    )
                    Text(
                        text = AppStr.calibrationSubtitle,
                        fontSize = 12.sp,
                        color = GeoTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = AppStr.close,
                        tint = GeoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Figure-8 Animation Canvas
            val theme = LocalAppTheme.current
            Box(
                modifier = Modifier
                    .size(220.dp, 130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(GeoSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(180.dp, 100.dp)) {
                    val w = size.width
                    val h = size.height
                    val centerX = w / 2f
                    val centerY = h / 2f
                    val a = w * 0.4f
                    val b = h * 0.35f

                    // Draw lemniscate (Figure-8) path
                    val path = Path()
                    val steps = 100
                    for (i in 0..steps) {
                        val t = (i.toFloat() / steps) * 2f * Math.PI.toFloat()
                        val scale = 2f / (3f - cos(2f * t))
                        val x = centerX + a * scale * cos(t)
                        val y = centerY + b * scale * sin(2f * t) / 2f
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    path.close()

                    drawPath(
                        path = path,
                        color = theme.border,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw moving device marker along figure-8
                    val currentT = animProgress * 2f * Math.PI.toFloat()
                    val currentScale = 2f / (3f - cos(2f * currentT))
                    val markerX = centerX + a * currentScale * cos(currentT)
                    val markerY = centerY + b * currentScale * sin(2f * currentT) / 2f
                    val markerCenter = Offset(markerX, markerY)

                    drawCircle(
                        color = theme.primary.copy(alpha = 0.25f),
                        radius = 14.dp.toPx(),
                        center = markerCenter
                    )
                    drawCircle(
                        color = theme.primary,
                        radius = 7.dp.toPx(),
                        center = markerCenter
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Sensor Accuracy Gauge
            val accuracyColor = when (accuracy) {
                SensorAccuracy.HIGH -> GeoSuccess
                SensorAccuracy.MEDIUM -> GeoWarning
                SensorAccuracy.LOW, SensorAccuracy.UNRELIABLE -> GeoError
            }

            val accuracyLabel = when (accuracy) {
                SensorAccuracy.HIGH -> AppStr.accuracyHigh
                SensorAccuracy.MEDIUM -> AppStr.accuracyMedium
                SensorAccuracy.LOW -> AppStr.accuracyLow
                SensorAccuracy.UNRELIABLE -> AppStr.accuracyUnreliable
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GeoSurfaceVariant)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = accuracyColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "${AppStr.sensorStatus}: $accuracyLabel",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = accuracyColor
                            )
                            Text(
                                text = if (accuracy == SensorAccuracy.HIGH) "Sensors are fully calibrated!" else AppStr.calibrationInstruction,
                                fontSize = 12.sp,
                                color = GeoTextSecondary
                            )
                        }
                    }

                    if (accuracy == SensorAccuracy.HIGH) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GeoSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Steps
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalibrationStepItem(
                    number = "1",
                    text = "Hold your phone flat away from metal objects or electronics."
                )
                CalibrationStepItem(
                    number = "2",
                    text = "Wave phone smoothly in a 3D figure-8 (∞) loop 3 to 5 times."
                )
                CalibrationStepItem(
                    number = "3",
                    text = "When high precision is achieved, your compass is ready."
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = AppStr.done,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CalibrationStepItem(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(GeoContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = GeoTextSecondary,
            lineHeight = 16.sp
        )
    }
}
