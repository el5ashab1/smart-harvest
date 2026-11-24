package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarvestAccentAmber
import com.example.ui.theme.HarvestAccentRed
import com.example.ui.theme.HarvestAccentWater
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestSecondary
import com.example.ui.theme.HarvestSurface
import com.example.ui.theme.HarvestTertiary

@Composable
fun SoilMoistureCircularGauge(
    currentMoisture: Int,
    threshold: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (currentMoisture / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "gauge_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_dot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse_alpha"
    )

    val statusText = when {
        currentMoisture < threshold - 5 -> "التربة بحاجة للري (${currentMoisture}% < ${threshold}%)"
        currentMoisture > threshold + 15 -> "التربة رطبة جداً ومكتفية"
        else -> "النظام يعمل بكفاءة - رطوبة مثالية"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("soil_moisture_gauge"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Gauge Canvas
        Box(
            modifier = Modifier.size(210.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(190.dp)
            ) {
                val strokeWidth = 14.dp.toPx()
                val diameter = size.width - strokeWidth
                val arcSize = Size(diameter, diameter)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                // Background Track Circle #E5F0E8
                drawArc(
                    color = HarvestBackground,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Active Progress Arc #1B4D3E
                drawArc(
                    color = HarvestPrimary,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Center Content: 68% and Soil Moisture Label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currentMoisture%",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    color = HarvestPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "رطوبة التربة",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = HarvestMutedText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Efficiency Status Pill with Pulsing Dot
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = HarvestPillBg,
            border = BorderStroke(1.dp, HarvestBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HarvestPrimary.copy(alpha = dotAlpha))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    color = HarvestPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MetricSummaryChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0x99FFFFFF), // bg-white/60
        border = BorderStroke(1.dp, Color(0x66FFFFFF)), // border-white/40
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HarvestMutedText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HarvestPrimary
            )
        }
    }
}

@Composable
fun WaterTankLevelCard(
    tankLevelPercent: Int,
    tankStatus: String,
    modifier: Modifier = Modifier
) {
    val progress = (tankLevelPercent / 100f).coerceIn(0f, 1f)
    val color = when {
        tankLevelPercent < 20 -> HarvestAccentRed
        tankLevelPercent < 40 -> HarvestAccentAmber
        else -> HarvestPrimary
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HarvestBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HarvestBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDamage,
                            contentDescription = "مستوى الخزان",
                            tint = HarvestPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مستوى خزان المياه",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestPrimary
                        )
                        Text(
                            text = "حساس الموجات فوق الصوتية (HC-SR04)",
                            fontSize = 11.sp,
                            color = HarvestMutedText
                        )
                    }
                }

                Surface(
                    color = HarvestPillBg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, HarvestBorder)
                ) {
                    Text(
                        text = "$tankStatus ($tankLevelPercent%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(HarvestBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun PumpActiveWaveAnimation(
    flowRate: Double,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pump_wave")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = HarvestPillBg,
        border = BorderStroke(1.dp, HarvestBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(HarvestPrimary.copy(alpha = pulseAlpha))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مضخة المياه قيد التشغيل (ESP32 Relay Active)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary
                )
            }

            Text(
                text = "$flowRate L/m",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = HarvestPrimary
            )
        }
    }
}

