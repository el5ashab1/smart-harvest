package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
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
import com.example.data.models.AiAgronomicAdvice
import com.example.ui.theme.HarvestAccentAmber
import com.example.ui.theme.HarvestAccentWater
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAdviceBottomSheet(
    advice: AiAgronomicAdvice?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onApplyOptimalThreshold: (Int) -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HarvestBackground,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("ai_advice_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(HarvestPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Agronomist",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "المستشار الزراعي الذكي",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestPrimary
                        )
                        Text(
                            text = "تحليل مدعوم بـ Gemini AI لبيانات الحساسات",
                            fontSize = 12.sp,
                            color = HarvestMutedText
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = HarvestMutedText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = HarvestPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "جاري استشارة الذكاء الاصطناعي وتحليل قراءات الحساسات...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = HarvestPrimary
                        )
                    }
                }
            } else if (advice != null) {
                // Crop Title Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = advice.plantName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                            Text(
                                text = "معدل كفاءة الري المتوقع: ${advice.irrigationEfficiencyScore}%",
                                fontSize = 12.sp,
                                color = HarvestMutedText,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = HarvestPillBg,
                            border = BorderStroke(1.dp, HarvestBorder)
                        ) {
                            Text(
                                text = "العتبة: ${advice.optimalMoistureThreshold}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Detail Cards
                AdviceDetailCard(
                    icon = Icons.Default.WbSunny,
                    title = "احتياجات ضوء الشمس",
                    content = advice.sunlightNeeds,
                    accentColor = HarvestPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AdviceDetailCard(
                    icon = Icons.Default.Speed,
                    title = "جدول وتكرار الري الموصى به",
                    content = advice.wateringFrequency,
                    accentColor = HarvestPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AdviceDetailCard(
                    icon = Icons.Default.Eco,
                    title = "التسميد والتغذية النباتية",
                    content = advice.fertilizerRecommendation,
                    accentColor = HarvestPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                AdviceDetailCard(
                    icon = Icons.Default.Warning,
                    title = "تنبيه مخاطر الآفات والأمراض",
                    content = advice.diseaseRiskAlert,
                    accentColor = HarvestPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Raw Expert Summary
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = HarvestPillBg,
                    border = BorderStroke(1.dp, HarvestBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "الخلاصة",
                            tint = HarvestPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = advice.rawAdviceText,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = HarvestPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Apply Threshold Button
                Button(
                    onClick = {
                        onApplyOptimalThreshold(advice.optimalMoistureThreshold)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HarvestPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_threshold_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تطبيق العتبة المقترحة (${advice.optimalMoistureThreshold}%) على الحساس",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AdviceDetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HarvestBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = HarvestPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = HarvestMutedText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = HarvestPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

