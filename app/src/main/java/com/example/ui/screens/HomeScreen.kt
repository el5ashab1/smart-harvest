package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CropAgronomicProfile
import com.example.data.models.LiveWeatherData
import com.example.data.models.PlantConfig
import com.example.data.models.SensorData
import com.example.data.models.SmartFarmingAdvisory
import com.example.data.models.SystemControl
import com.example.data.models.ZoneInfo
import com.example.ui.components.LiveWeatherAndAdvisoryCard
import com.example.ui.components.MetricSummaryChip
import com.example.ui.components.PumpActiveWaveAnimation
import com.example.ui.components.SoilMoistureCircularGauge
import com.example.ui.components.WaterTankLevelCard
import com.example.ui.theme.HarvestAccentRed
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    plant: PlantConfig,
    sensorData: SensorData,
    control: SystemControl,
    isEspConnected: Boolean,
    liveWeather: LiveWeatherData?,
    cropProfile: CropAgronomicProfile?,
    advisory: SmartFarmingAdvisory?,
    isEvaluatingAdvisory: Boolean,
    zones: List<ZoneInfo> = emptyList(),
    onSelectZone: (ZoneInfo) -> Unit = {},
    onForceIrrigateZone: (ZoneInfo, Boolean) -> Unit = { _, _ -> },
    onRefreshEvaluation: () -> Unit,
    onToggleManualOverride: (Boolean) -> Unit,
    onTogglePump: (Boolean) -> Unit,
    onOpenAiAdvice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showZoneSelectorSheet by remember { mutableStateOf(false) }
    val zoneSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Find active zone info if matches
    val activeZone = zones.find { it.name.contains(plant.zone) || plant.zone.contains(it.name) } ?: zones.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HarvestBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Professional Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "حصادك الذكي",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = if (isEspConnected) "مزامن مع ESP32 • متصل" else "جاري الاتصال بـ ESP32...",
                    fontSize = 12.sp,
                    color = if (isEspConnected) HarvestMutedText else HarvestAccentRed,
                    fontWeight = FontWeight.Medium
                )
            }

            // User Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HarvestPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "الملف الشخصي",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Interactive Active Zone Selector Card (Click to change zone focus)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showZoneSelectorSheet = true }
                .testTag("zone_selector_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HarvestPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = HarvestPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "المنطقة النشطة:",
                                fontSize = 11.sp,
                                color = HarvestMutedText,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(انقر للتبديل)",
                                fontSize = 10.sp,
                                color = HarvestPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = plant.zone,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF121212)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HarvestPillBg,
                    border = BorderStroke(1.dp, HarvestBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (activeZone?.valveStatus == "OPEN") "الصمام مفتوح" else "صمام مغلق",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeZone?.valveStatus == "OPEN") Color(0xFF2E7D32) else HarvestPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = HarvestPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Top 3 Metric Summary Chips Grid (Unified Temperature with WeatherAPI)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricSummaryChip(
                title = "الحرارة الحقلية",
                value = "${(liveWeather?.tempC ?: sensorData.airTemperature)}°C",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryChip(
                title = "رطوبة الجو",
                value = "${(liveWeather?.humidity ?: sensorData.airHumidity)}%",
                modifier = Modifier.weight(1f)
            )
            MetricSummaryChip(
                title = "مستوى الخزان",
                value = "${sensorData.tankLevelPercent}%",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Soil Moisture & Plant Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Plant Info Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "المحصول المختار",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HarvestMutedText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = plant.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF121212)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HarvestPillBg,
                        border = BorderStroke(1.dp, HarvestBorder)
                    ) {
                        Text(
                            text = "العتبة: ${plant.moistureThreshold}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Circular Soil Moisture Gauge
                SoilMoistureCircularGauge(
                    currentMoisture = sensorData.soilMoisture,
                    threshold = plant.moistureThreshold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pump Active Alert (if running)
        AnimatedVisibility(
            visible = control.pumpStatus == "ON",
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                PumpActiveWaveAnimation(flowRate = sensorData.flowRateLpm)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Manual Irrigation & Forced Zone Watering Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = HarvestPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "التحكم بالري والمضخة",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (control.pumpStatus == "ON") "المضخة: تعمل الآن (${sensorData.flowRateLpm} L/m)" else "المضخة: متوقفة الآن",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Switch(
                        checked = control.manualOverride,
                        onCheckedChange = { onToggleManualOverride(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HarvestPrimary,
                            checkedTrackColor = Color.White,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.25f),
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.testTag("manual_override_toggle")
                    )
                }

                // Action Buttons for Manual Pump Trigger & Zone Forced Watering
                val isPumpOn = control.pumpStatus == "ON"
                val isCurrentZoneActive = activeZone?.valveStatus == "OPEN" && isPumpOn

                Column(modifier = Modifier.padding(top = 14.dp)) {
                    // Row with Force Water On for Selected Zone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Force Water Zone Button
                        Button(
                            onClick = {
                                if (activeZone != null) {
                                    onForceIrrigateZone(activeZone, !isCurrentZoneActive)
                                } else {
                                    onTogglePump(!isPumpOn)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentZoneActive) HarvestAccentRed else Color.White,
                                contentColor = if (isCurrentZoneActive) Color.White else HarvestPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("force_water_zone_button")
                        ) {
                            Icon(
                                imageVector = if (isCurrentZoneActive) Icons.Default.Stop else Icons.Default.WaterDrop,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCurrentZoneActive) "إيقاف الري الإجباري" else "تشغيل المياه إجباري",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 2. Direct Pump Power Button
                        Button(
                            onClick = { onTogglePump(!isPumpOn) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPumpOn) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.9f),
                                contentColor = if (isPumpOn) Color.White else HarvestPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("pump_power_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isPumpOn) "إيقاف المضخة" else "تشغيل المضخة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Water Tank Level Card (Detailed)
        WaterTankLevelCard(
            tankLevelPercent = sensorData.tankLevelPercent,
            tankStatus = sensorData.tankStatus
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Real-time WeatherAPI + Gemini AI Agronomic Advisory Card (Unified Temp Source)
        LiveWeatherAndAdvisoryCard(
            liveWeather = liveWeather,
            cropProfile = cropProfile,
            advisory = advisory,
            isEvaluating = isEvaluatingAdvisory,
            onRefreshEvaluation = onRefreshEvaluation
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AI Advice Action Button
        Surface(
            onClick = onOpenAiAdvice,
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = BorderStroke(2.dp, HarvestPrimary.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("open_ai_advice_button")
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "استشارة الذكاء الاصطناعي",
                    tint = HarvestPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "اطلب استشارة الذكاء الاصطناعي",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Modal Bottom Sheet to Select Zone / Greenhouse
    if (showZoneSelectorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showZoneSelectorSheet = false },
            sheetState = zoneSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "اختر المنطقة / الصوبة الحالية",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
                Text(
                    text = "تحديد منطقة العمل لتوجيه الري وقراءات الحساسات",
                    fontSize = 12.sp,
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(16.dp))

                zones.forEach { zone ->
                    val isSelected = plant.zone.contains(zone.name) || zone.name.contains(plant.zone)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) HarvestPrimary.copy(alpha = 0.08f) else Color.White
                        ),
                        border = if (isSelected) BorderStroke(1.5.dp, HarvestPrimary) else BorderStroke(1.dp, HarvestBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectZone(zone)
                                coroutineScope.launch { zoneSheetState.hide() }.invokeOnCompletion {
                                    showZoneSelectorSheet = false
                                }
                            }
                            .padding(vertical = 4.dp)
                            .testTag("zone_select_item_${zone.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) HarvestPrimary else HarvestBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else HarvestPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = zone.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF121212)
                                    )
                                    Text(
                                        text = "${zone.activePlant} • ${zone.areaSqm} م² • رطوبة: ${zone.currentMoisture}%",
                                        fontSize = 11.sp,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (zone.valveStatus == "OPEN") Color(0xFFE8F5E9) else HarvestPillBg,
                                border = BorderStroke(1.dp, if (zone.valveStatus == "OPEN") Color(0xFF2E7D32) else HarvestBorder)
                            ) {
                                Text(
                                    text = if (zone.valveStatus == "OPEN") "الصمام مفتوح" else "صمام مغلق",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (zone.valveStatus == "OPEN") Color(0xFF2E7D32) else HarvestPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
