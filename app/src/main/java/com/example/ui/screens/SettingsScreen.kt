package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarvestAccentAmber
import com.example.ui.theme.HarvestAccentWater
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestPrimaryDark
import com.example.ui.theme.HarvestSecondary
import com.example.ui.theme.HarvestTertiary

@Composable
fun SettingsScreen(
    isEspConnected: Boolean,
    isSimulationActive: Boolean,
    notificationsEnabled: Boolean,
    lowWaterAlertEnabled: Boolean,
    onToggleSimulation: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleLowWaterAlert: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HarvestBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("settings_screen")
    ) {
        // Header
        Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
            Text(
                text = "الإعدادات وتشخيص النظام",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HarvestPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "حالة اتصال متحكم ESP32، إشعارات الأمان، وقاعدة بيانات Firebase",
                fontSize = 12.sp,
                color = HarvestMutedText,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Diagnostics Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(HarvestBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeveloperBoard,
                                contentDescription = null,
                                tint = HarvestPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "متحكم ESP32 NodeMCU",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                            Text(
                                text = "اتصال عبر Wi-Fi و Firebase",
                                fontSize = 11.sp,
                                color = HarvestMutedText
                            )
                        }
                    }

                    Surface(
                        color = if (isEspConnected) HarvestBackground else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isEspConnected) HarvestBorder else Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = if (isEspConnected) "متصل • Live" else "غير متصل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEspConnected) HarvestPrimary else Color(0xFFD97706),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = HarvestBorder.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))

                DiagnosticRow(label = "عنوان IP المحلي", value = "192.168.1.105 (Port 80)")
                DiagnosticRow(label = "عنوان MAC للعتاد", value = "24:6F:28:B1:3A:90")
                DiagnosticRow(label = "قوة إشارة Wi-Fi", value = "-62 dBm (ممتازة)")
                DiagnosticRow(label = "معدل تحديث الحساسات", value = "كل 3 ثوانٍ")
                DiagnosticRow(label = "عقدة Firebase RTDB", value = "https://smartharvest-rtdb.firebaseio.com")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sensors Hardware Mapping Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الحساسات والمنافذ (Hardware Pins)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SensorPinRow("حساس رطوبة التربة السعوي (Capacitive v1.2)", "GPIO 34 (ADC1)")
                SensorPinRow("حساس الحرارة والرطوبة (DHT11 Digital)", "GPIO 4")
                SensorPinRow("حساس مستوى الخزان (Ultrasonic HC-SR04)", "Trig: GPIO 5 / Echo: GPIO 18")
                SensorPinRow("حساس تدفق المياه (YF-S201 Flow Sensor)", "GPIO 19 (Interrupt)")
                SensorPinRow("مرحل تشغيل المضخة (5V Relay Module)", "GPIO 23 (Active LOW)")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Alerts & Notification Toggles Card
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التنبيهات والإشعارات الفورية (Alerts)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SettingSwitchRow(
                    title = "إشعارات انخفاض ماء الخزان",
                    subtitle = "تنبيه فوري عند وصول مستوى الخزان لأقل من 20%",
                    checked = lowWaterAlertEnabled,
                    onCheckedChange = { onToggleLowWaterAlert(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = HarvestBorder.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    title = "إشعارات تعطل الحساسات أو انقطاع ESP32",
                    subtitle = "تنبيه عند انقطاع الاتصال لأكثر من 60 ثانية",
                    checked = notificationsEnabled,
                    onCheckedChange = { onToggleNotifications(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = HarvestBorder.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(12.dp))

                SettingSwitchRow(
                    title = "محاكاة الحساسات الحقلية (Simulation Mode)",
                    subtitle = "توليد قراءات ديناميكية لتجربة النظام واختبار المضخة دون عتاد فعلي",
                    checked = isSimulationActive,
                    onCheckedChange = { onToggleSimulation(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = HarvestMutedText)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HarvestPrimary)
    }
}

@Composable
private fun SensorPinRow(sensor: String, pin: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = sensor, fontSize = 12.sp, color = HarvestPrimary, fontWeight = FontWeight.Medium)
        Surface(
            color = HarvestPillBg,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, HarvestBorder)
        ) {
            Text(
                text = pin,
                fontSize = 11.sp,
                color = HarvestPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HarvestPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = HarvestMutedText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HarvestPrimary,
                uncheckedThumbColor = HarvestMutedText,
                uncheckedTrackColor = HarvestBorder
            )
        )
    }
}

