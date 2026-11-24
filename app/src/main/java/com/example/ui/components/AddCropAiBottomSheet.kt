package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AiCropCareInfo
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCropAiBottomSheet(
    onDismiss: () -> Unit,
    onFetchAiCareInfo: suspend (String) -> AiCropCareInfo,
    onConfirmAndSetActiveCrop: (
        plantName: String,
        moistureThreshold: Int,
        zoneName: String,
        targetMaxMoisture: Int,
        wateringDurationMins: Int,
        sunlightNeeds: String,
        careTips: String
    ) -> Unit,
    sheetState: SheetState
) {
    val coroutineScope = rememberCoroutineScope()
    var plantNameInput by remember { mutableStateOf("") }
    var zoneNameInput by remember { mutableStateOf("") }

    var isAnalyzing by remember { mutableStateOf(false) }
    var analyzedResult by remember { mutableStateOf<AiCropCareInfo?>(null) }
    var editableThreshold by remember { mutableFloatStateOf(50f) }

    val quickCropSuggestions = listOf("طماطم شيري", "نعناع بلدي", "خيار هجين", "فلفل حار", "فراولة", "ريحان")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HarvestBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(HarvestBorder)
            )
        }
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .imePadding()
                    .testTag("add_crop_bottom_sheet")
            ) {
                // Header Bar
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
                                contentDescription = "إضافة محصول جديد",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "إضافة / اختيار محصول جديد",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                            Text(
                                text = "تحديد عتبة الري الذكية عبر Gemini AI",
                                fontSize = 12.sp,
                                color = HarvestMutedText
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_crop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = HarvestMutedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Input Field 1: Plant Name
                Text(
                    text = "اسم المحصول أو النبتة",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = plantNameInput,
                    onValueChange = {
                        plantNameInput = it
                        // Reset analyzed result when input changes
                        if (analyzedResult != null && analyzedResult?.plantName != it) {
                            analyzedResult = null
                        }
                    },
                    placeholder = {
                        Text(
                            text = "مثال: طماطم شيري، نعناع، خيار...",
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF121212),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = HarvestPrimary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF121212),
                        unfocusedTextColor = Color(0xFF121212),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = HarvestPrimary,
                        unfocusedBorderColor = HarvestBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plant_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Suggestion Chips
                Text(
                    text = "اقتراحات سريعة:",
                    fontSize = 11.sp,
                    color = Color(0xFF424242),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickCropSuggestions.take(3).forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (plantNameInput == suggestion) HarvestPrimary else Color.White,
                            border = BorderStroke(1.dp, if (plantNameInput == suggestion) HarvestPrimary else HarvestBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    plantNameInput = suggestion
                                    analyzedResult = null
                                }
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (plantNameInput == suggestion) Color.White else Color(0xFF121212),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Field 2: Zone / Location (Optional)
                Text(
                    text = "المنطقة / الموقع (اختياري)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = zoneNameInput,
                    onValueChange = { zoneNameInput = it },
                    placeholder = {
                        Text(
                            text = "مثال: الصوبة 1 - خط A أو الحديقة",
                            color = Color(0xFF424242),
                            fontWeight = FontWeight.Medium
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF121212),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = HarvestPrimary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF121212),
                        unfocusedTextColor = Color(0xFF121212),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = HarvestPrimary,
                        unfocusedBorderColor = HarvestBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("zone_name_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Trigger AI Analysis Action Button
                if (analyzedResult == null && !isAnalyzing) {
                    Button(
                        onClick = {
                            if (plantNameInput.isNotBlank()) {
                                isAnalyzing = true
                                coroutineScope.launch {
                                    val res = onFetchAiCareInfo(plantNameInput.trim())
                                    analyzedResult = res
                                    editableThreshold = res.moistureThreshold.toFloat()
                                    isAnalyzing = false
                                }
                            }
                        },
                        enabled = plantNameInput.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HarvestPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = HarvestBorder,
                            disabledContentColor = HarvestMutedText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("fetch_ai_care_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تحليل متطلبات النبات بالذكاء الاصطناعي",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Loading State with Smooth Indicator
                AnimatedVisibility(
                    visible = isAnalyzing,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = HarvestPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "جارٍ تحليل متطلبات النبات بواسطة الذكاء الاصطناعي...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يتم حساب عتبة الرطوبة المثالية وساعات الشمس وجدول الري",
                                fontSize = 11.sp,
                                color = HarvestMutedText
                            )
                        }
                    }
                }

                // AI Review & Configuration State
                analyzedResult?.let { careInfo ->
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, HarvestPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = HarvestSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "نتائج التحليل الزراعي لـ ${careInfo.plantName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HarvestPrimary
                                    )
                                }

                                Surface(
                                    color = HarvestPillBg,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, HarvestBorder)
                                ) {
                                    Text(
                                        text = "AI Verified",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HarvestPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Moisture Threshold Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "عتبة تشغيل الري (Moisture Threshold)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HarvestPrimary
                                    )
                                    Text(
                                        text = "الموصى به: ${careInfo.moistureThreshold}% (يمكنك تعديلها يدوياً)",
                                        fontSize = 11.sp,
                                        color = HarvestMutedText
                                    )
                                }
                                Surface(
                                    color = HarvestPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${editableThreshold.toInt()}%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Slider(
                                value = editableThreshold,
                                onValueChange = { editableThreshold = it },
                                valueRange = 20f..85f,
                                steps = 12,
                                colors = SliderDefaults.colors(
                                    thumbColor = HarvestPrimary,
                                    activeTrackColor = HarvestPrimary,
                                    inactiveTrackColor = HarvestBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = HarvestBorder.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(10.dp))

                            // 2-Column Care Specs Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Target max moisture
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HarvestBackground,
                                    border = BorderStroke(1.dp, HarvestBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "أقصى رطوبة مستهدفة", fontSize = 10.sp, color = HarvestMutedText)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "${careInfo.targetMaxMoisture}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HarvestPrimary)
                                    }
                                }

                                // Recommended watering duration
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HarvestBackground,
                                    border = BorderStroke(1.dp, HarvestBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = "مدة دورة الري", fontSize = 10.sp, color = HarvestMutedText)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = "${careInfo.recommendedWateringDurationMins} دقيقة", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HarvestPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sunlight requirement row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(HarvestPillBg, RoundedCornerShape(10.dp))
                                    .border(1.dp, HarvestBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = HarvestPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "احتياج ضوء الشمس:", fontSize = 10.sp, color = HarvestMutedText, fontWeight = FontWeight.Bold)
                                    Text(text = careInfo.sunlightNeeds, fontSize = 11.sp, color = HarvestPrimary, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Care tips row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(HarvestBackground, RoundedCornerShape(10.dp))
                                    .border(1.dp, HarvestBorder, RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Grass,
                                    contentDescription = null,
                                    tint = HarvestSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = careInfo.careTips,
                                    fontSize = 11.sp,
                                    color = HarvestPrimary,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Confirm CTA Button (Firebase Realtime Database write trigger)
                            Button(
                                onClick = {
                                    onConfirmAndSetActiveCrop(
                                        plantNameInput.trim(),
                                        editableThreshold.toInt(),
                                        if (zoneNameInput.isNotBlank()) zoneNameInput.trim() else "المنطقة الأولى - البيت المحمي",
                                        careInfo.targetMaxMoisture,
                                        careInfo.recommendedWateringDurationMins,
                                        careInfo.sunlightNeeds,
                                        careInfo.careTips
                                    )
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HarvestPrimary,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("confirm_set_crop_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تأكيد وتفعيل المحصول في النظام",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
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
