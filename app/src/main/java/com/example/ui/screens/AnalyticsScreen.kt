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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WateringLog
import com.example.ui.theme.HarvestAccentWater
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestPrimaryDark
import com.example.ui.theme.HarvestSecondary
import com.example.ui.theme.HarvestTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    wateringLogs: List<WateringLog>,
    modifier: Modifier = Modifier
) {
    val totalLitersThisWeek = wateringLogs.sumOf { it.waterVolumeLiters } + 28.4
    val daysData = listOf(
        Pair("السبت", 4.2),
        Pair("الأحد", 5.8),
        Pair("الإثنين", 3.6),
        Pair("الثلاثاء", 6.2),
        Pair("الأربعاء", 4.0),
        Pair("الخميس", 5.1),
        Pair("اليوم", 3.8)
    )
    val maxScaleLiters = 8.0 // Synchronized uniform max scale for Y-axis

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HarvestBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("analytics_screen")
    ) {
        item {
            // Header
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                Text(
                    text = "تحليلات استهلاك المياه",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "سجلات حساس التدفق YF-S201 وتاريخ دورات الري الموحد",
                    fontSize = 12.sp,
                    color = HarvestMutedText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Water Savings Hero Stat Card
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = HarvestPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = Color(0xFFA7F3D0),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "توفير المياه بالري التلقائي",
                                fontSize = 12.sp,
                                color = Color(0xFFA7F3D0),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "38.5%",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "تم توفير ~142 لتراً مقارنة بالري التقليدي",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Weekly Water Consumption Bar Chart with Synchronized Baseline & Y-Axis Scale
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
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = HarvestPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "استهلاك المياه الأسبوعي (لتر)",
                                fontSize = 14.sp,
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
                                text = "إجمالي: ${(totalLitersThisWeek * 10).toInt() / 10.0} L",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Precision Chart Area: Synchronized Y-Axis Grid + Uniform Baseline
                    val chartHeight = 130.dp

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(chartHeight + 40.dp)
                    ) {
                        // Left Y-Axis Scale Labels (8L, 6L, 4L, 2L, 0L)
                        Column(
                            modifier = Modifier
                                .height(chartHeight)
                                .padding(end = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(text = "8L", fontSize = 9.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                            Text(text = "6L", fontSize = 9.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                            Text(text = "4L", fontSize = 9.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                            Text(text = "2L", fontSize = 9.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                            Text(text = "0L", fontSize = 9.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                        }

                        // Chart Main Canvas with Horizontal Grid Guides & Aligned Columns
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {
                            // Upper Section with bars & background horizontal gridlines
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(chartHeight)
                            ) {
                                // Background Horizontal Gridlines
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    HorizontalDivider(color = HarvestBorder.copy(alpha = 0.4f), thickness = 0.8.dp)
                                    HorizontalDivider(color = HarvestBorder.copy(alpha = 0.3f), thickness = 0.8.dp)
                                    HorizontalDivider(color = HarvestBorder.copy(alpha = 0.3f), thickness = 0.8.dp)
                                    HorizontalDivider(color = HarvestBorder.copy(alpha = 0.3f), thickness = 0.8.dp)
                                    HorizontalDivider(color = HarvestBorder.copy(alpha = 0.8f), thickness = 1.2.dp) // Baseline
                                }

                                // Synchronized Bars along baseline
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    daysData.forEach { (day, liters) ->
                                        val heightFraction = (liters / maxScaleLiters).toFloat().coerceIn(0.08f, 1.0f)
                                        val isToday = day == "اليوم"

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Bottom
                                        ) {
                                            Text(
                                                text = "$liters",
                                                fontSize = 10.sp,
                                                color = if (isToday) HarvestPrimary else Color(0xFF424242),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Box(
                                                modifier = Modifier
                                                    .width(26.dp)
                                                    .height((chartHeight - 22.dp) * heightFraction)
                                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                                    .background(
                                                        if (isToday) HarvestPrimary else HarvestPrimary.copy(alpha = 0.35f)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }

                            // Aligned Day Labels beneath baseline
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                daysData.forEach { (day, _) ->
                                    val isToday = day == "اليوم"
                                    Text(
                                        text = day,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(34.dp),
                                        color = if (isToday) HarvestPrimary else Color(0xFF424242),
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Historical Logs Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سجل عمليات الري المتزامنة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF121212)
                    )
                }

                Surface(
                    color = HarvestPillBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, HarvestBorder)
                ) {
                    Text(
                        text = "${wateringLogs.size} دورات",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // List of Logs
        items(wateringLogs) { log ->
            WateringLogItemCard(log = log)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WateringLogItemCard(log: WateringLog) {
    val formatter = SimpleDateFormat("yyyy/MM/dd • hh:mm a", Locale("ar"))
    val dateString = formatter.format(Date(log.timestamp))

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
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
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = log.triggerType,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF121212)
                    )
                    Text(
                        text = "$dateString • ${log.durationSeconds} ثانية",
                        fontSize = 11.sp,
                        color = Color(0xFF424242)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HarvestPillBg,
                border = BorderStroke(1.dp, HarvestBorder)
            ) {
                Text(
                    text = "${log.waterVolumeLiters} L",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = HarvestPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
