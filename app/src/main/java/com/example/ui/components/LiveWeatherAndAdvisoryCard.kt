package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AdvisorySeverity
import com.example.data.models.CropAgronomicProfile
import com.example.data.models.LiveWeatherData
import com.example.data.models.SmartFarmingAdvisory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveWeatherAndAdvisoryCard(
    liveWeather: LiveWeatherData?,
    cropProfile: CropAgronomicProfile?,
    advisory: SmartFarmingAdvisory?,
    isEvaluating: Boolean,
    onRefreshEvaluation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("live_weather_advisory_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header: Location, Live Telemetry Tag & Refresh Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0288D1).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "WeatherAPI Telemetry",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = liveWeather?.locationName ?: "محطة الأرصاد الحقلية",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Text(
                                text = "بيانات حية من WeatherAPI + تقييم Gemini AI",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefreshEvaluation,
                        enabled = !isEvaluating,
                        modifier = Modifier.testTag("refresh_weather_advisory_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة التقييم والمزامنة",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = if (isEvaluating) Modifier.rotate(rotation) else Modifier
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weather Telemetry Grid (Temp, Humidity, Condition, Wind)
                if (liveWeather != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0288D1).copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Temperature
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Thermostat,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${liveWeather.tempC}°C",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "الحرارة الحالية",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Humidity
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Opacity,
                                        contentDescription = null,
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${liveWeather.humidity}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "رطوبة الجو",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Condition
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = Color(0xFFF57C00),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = liveWeather.conditionText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "حالة الطقس",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Wind
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Air,
                                        contentDescription = null,
                                        tint = Color(0xFF546E7A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${liveWeather.windKph} كم/س",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = "الرياح",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Crop Ideal Agronomic Snapshot from Gemini
                if (cropProfile != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF2E7D32).copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "المحددات المثالية للمحصول (${cropProfile.cropName}):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AgronomicTag(
                                    label = "الرطوبة المثالية: ${cropProfile.idealSoilMoistureMin}% - ${cropProfile.idealSoilMoistureMax}%",
                                    icon = Icons.Default.Opacity,
                                    accentColor = Color(0xFF0288D1)
                                )
                                AgronomicTag(
                                    label = "الحرارة المثالية: ${cropProfile.idealTempMin}°C - ${cropProfile.idealTempMax}°C",
                                    icon = Icons.Default.Thermostat,
                                    accentColor = Color(0xFFE65100)
                                )
                                AgronomicTag(
                                    label = "الرطوبة الجوية: ${cropProfile.idealHumidityRange}",
                                    icon = Icons.Default.Cloud,
                                    accentColor = Color(0xFF5E35B1)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cross-Referenced Smart Advisory Box
                if (advisory != null) {
                    val (badgeBg, badgeBorder, badgeTextColor, badgeIcon) = when (advisory.severity) {
                        AdvisorySeverity.CRITICAL -> Quadruple(
                            Color(0xFFFFEBEE),
                            Color(0xFFD32F2F),
                            Color(0xFFC62828),
                            Icons.Default.Warning
                        )
                        AdvisorySeverity.WARNING -> Quadruple(
                            Color(0xFFFFF3E0),
                            Color(0xFFEF6C00),
                            Color(0xFFE65100),
                            Icons.Default.Warning
                        )
                        AdvisorySeverity.ATTENTION -> Quadruple(
                            Color(0xFFE3F2FD),
                            Color(0xFF1976D2),
                            Color(0xFF0D47A1),
                            Icons.Default.Info
                        )
                        AdvisorySeverity.OPTIMAL -> Quadruple(
                            Color(0xFFE8F5E9),
                            Color(0xFF388E3C),
                            Color(0xFF1B5E20),
                            Icons.Default.CheckCircle
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = badgeBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeBorder,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = advisory.headline,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Dynamic Recommendation
                            Text(
                                text = "• التوصية التشغيلية: ${advisory.moistureAdjustmentRecommendation}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = badgeTextColor,
                                    fontSize = 13.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "• مؤشر التبخر النتحي: ${advisory.evaporationRisk} | حالة الإجهاد: ${advisory.temperatureStressStatus}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = badgeTextColor.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            )

                            if (advisory.actionableSteps.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    advisory.actionableSteps.take(2).forEach { step ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "✓",
                                                color = badgeBorder,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = step,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (isEvaluating) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جارٍ مطابقة الأرصاد الحية مع متطلبات المحصول عبر Gemini...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgronomicTag(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = accentColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
