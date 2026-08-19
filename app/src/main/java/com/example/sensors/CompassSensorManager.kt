package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import com.example.model.CompassReading
import com.example.model.SensorAccuracy
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CompassSensorManager(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager

    /**
     * Emits continuous compass orientation readings fusing rotation vector or accelerometer + magnetometer.
     */
    fun getCompassOrientationFlow(): Flow<CompassReading> = callbackFlow {
        if (sensorManager == null) {
            trySend(CompassReading())
            awaitClose { }
            return@callbackFlow
        }

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val rotationMatrix = FloatArray(9)
        val adjustedRotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        // Low-pass smoothing state
        var smoothedAzimuthSin = 0.0
        var smoothedAzimuthCos = 1.0
        var smoothedPitch = 0f
        var smoothedRoll = 0f
        var isFirstSample = true
        var currentAccuracy = SensorAccuracy.HIGH
        val alpha = 0.15f // Smoothing factor

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                var hasValidMatrix = false

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    hasValidMatrix = true
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, 3)
                    hasGravity = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                    hasGeomagnetic = true
                }

                if (!hasValidMatrix && hasGravity && hasGeomagnetic) {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                        hasValidMatrix = true
                    }
                }

                if (hasValidMatrix) {
                    // Remap coordinate system based on device rotation
                    val rotation = windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
                    when (rotation) {
                        Surface.ROTATION_0 -> {
                            SensorManager.remapCoordinateSystem(
                                rotationMatrix,
                                SensorManager.AXIS_X,
                                SensorManager.AXIS_Y,
                                adjustedRotationMatrix
                            )
                        }
                        Surface.ROTATION_90 -> {
                            SensorManager.remapCoordinateSystem(
                                rotationMatrix,
                                SensorManager.AXIS_Y,
                                SensorManager.AXIS_MINUS_X,
                                adjustedRotationMatrix
                            )
                        }
                        Surface.ROTATION_180 -> {
                            SensorManager.remapCoordinateSystem(
                                rotationMatrix,
                                SensorManager.AXIS_MINUS_X,
                                SensorManager.AXIS_MINUS_Y,
                                adjustedRotationMatrix
                            )
                        }
                        Surface.ROTATION_270 -> {
                            SensorManager.remapCoordinateSystem(
                                rotationMatrix,
                                SensorManager.AXIS_MINUS_Y,
                                SensorManager.AXIS_X,
                                adjustedRotationMatrix
                            )
                        }
                        else -> {
                            System.arraycopy(rotationMatrix, 0, adjustedRotationMatrix, 0, 9)
                        }
                    }

                    SensorManager.getOrientation(adjustedRotationMatrix, orientationAngles)

                    val rawAzimuthDeg = (Math.toDegrees(orientationAngles[0].toDouble()) + 360.0) % 360.0
                    val rawPitchDeg = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    val rawRollDeg = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                    // Circular exponential moving average for smooth needle motion
                    val rad = Math.toRadians(rawAzimuthDeg)
                    if (isFirstSample) {
                        smoothedAzimuthSin = sin(rad)
                        smoothedAzimuthCos = cos(rad)
                        smoothedPitch = rawPitchDeg
                        smoothedRoll = rawRollDeg
                        isFirstSample = false
                    } else {
                        smoothedAzimuthSin = smoothedAzimuthSin * (1.0 - alpha) + sin(rad) * alpha
                        smoothedAzimuthCos = smoothedAzimuthCos * (1.0 - alpha) + cos(rad) * alpha
                        smoothedPitch = smoothedPitch * (1f - alpha) + rawPitchDeg * alpha
                        smoothedRoll = smoothedRoll * (1f - alpha) + rawRollDeg * alpha
                    }
                    val smoothAzimuth = (Math.toDegrees(atan2(smoothedAzimuthSin, smoothedAzimuthCos)) + 360.0) % 360.0

                    val isLevel = abs(smoothedPitch) < 18f && abs(smoothedRoll) < 18f

                    trySend(
                        CompassReading(
                            azimuth = smoothAzimuth.toFloat(),
                            pitch = smoothedPitch,
                            roll = smoothedRoll,
                            accuracy = currentAccuracy,
                            isLevel = isLevel
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD || sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    currentAccuracy = SensorAccuracy.fromSensorAccuracy(accuracy)
                }
            }
        }

        val samplingPeriodUs = SensorManager.SENSOR_DELAY_UI

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(listener, rotationVectorSensor, samplingPeriodUs)
        } else {
            accelerometerSensor?.let { sensorManager.registerListener(listener, it, samplingPeriodUs) }
            magneticSensor?.let { sensorManager.registerListener(listener, it, samplingPeriodUs) }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
