package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PlantConfig
import com.example.data.models.ZoneInfo
import com.example.ui.theme.HarvestAccentRed
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestBorder
import com.example.ui.theme.HarvestMutedText
import com.example.ui.theme.HarvestPillBg
import com.example.ui.theme.HarvestPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantsZonesScreen(
    currentPlant: PlantConfig,
    savedCrops: List<PlantConfig>,
    zones: List<ZoneInfo>,
    onSelectPlant: (PlantConfig) -> Unit,
    onSelectZone: (ZoneInfo) -> Unit = {},
    onRenameZone: (String, String) -> Unit = { _, _ -> },
    onDeleteZone: (String) -> Unit = {},
    onAddZone: (String, String, Double) -> Unit = { _, _, _ -> },
    onUpdateThreshold: (Int) -> Unit,
    onRequestAiAdvice: (String) -> Unit,
    onOpenAddCropAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember(currentPlant.moistureThreshold) {
        mutableFloatStateOf(currentPlant.moistureThreshold.toFloat())
    }

    // State for Context Menu and Dialogs
    var contextMenuZone by remember { mutableStateOf<ZoneInfo?>(null) }
    var zoneToRename by remember { mutableStateOf<ZoneInfo?>(null) }
    var zoneToDelete by remember { mutableStateOf<ZoneInfo?>(null) }
    var showAddZoneDialog by remember { mutableStateOf(false) }

    // Dialog state holders
    var renameInputText by remember { mutableStateOf("") }
    var newZoneName by remember { mutableStateOf("") }
    var newZoneCrop by remember { mutableStateOf("") }
    var newZoneArea by remember { mutableStateOf("25.0") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HarvestBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("plants_zones_screen")
    ) {
        item {
            // Screen Header
            Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                Text(
                    text = "إدارة المحاصيل والمناطق",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "تعديل عتبة الرطوبة المطلوبة، واختيار المحصول أو الصوبة",
                    fontSize = 12.sp,
                    color = HarvestMutedText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prominent "Add / Change Active Crop" Button Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HarvestPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAddCropAi() }
                    .testTag("open_add_crop_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "إضافة / اختيار محصول جديد",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "تحليل متطلبات المحصول عبر الذكاء الاصطناعي وتحديث السحابة",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = HarvestPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "إضافة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Crop Moisture Threshold Setting Card
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
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = HarvestPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "العتبة الحرجة للري (الرطوبة)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestPrimary
                                )
                                Text(
                                    text = currentPlant.name,
                                    fontSize = 11.sp,
                                    color = HarvestMutedText
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HarvestPillBg,
                            border = BorderStroke(1.dp, HarvestBorder)
                        ) {
                            Text(
                                text = "${sliderValue.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = HarvestPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { onUpdateThreshold(sliderValue.toInt()) },
                        valueRange = 15f..85f,
                        steps = 14,
                        colors = SliderDefaults.colors(
                            thumbColor = HarvestPrimary,
                            activeTrackColor = HarvestPrimary,
                            inactiveTrackColor = HarvestBorder
                        ),
                        modifier = Modifier.testTag("moisture_threshold_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "15% (جاف جداً)", fontSize = 10.sp, color = HarvestMutedText)
                        Text(text = "عتبة تشغيل المضخة التلقائي", fontSize = 11.sp, color = HarvestPrimary, fontWeight = FontWeight.SemiBold)
                        Text(text = "85% (مشبع)", fontSize = 10.sp, color = HarvestMutedText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Crops Switcher Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المحاصيل المحفوظة والنشطة",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarvestPrimary
                )
                Text(
                    text = "${savedCrops.size} محاصيل مسجلة",
                    fontSize = 11.sp,
                    color = HarvestMutedText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Saved & Dynamic Crops List
        items(savedCrops) { crop ->
            val isSelected = crop.name == currentPlant.name

            CropItemCard(
                crop = crop,
                isSelected = isSelected,
                onSelect = { onSelectPlant(crop) },
                onConsultAi = { onRequestAiAdvice(crop.name) }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Zones Management Header with Add Zone action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مناطق وصمامات الري (Irrigation Zones)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary
                    )
                    Text(
                        text = "اضغط مطولاً على أي منطقة لتعديل الاسم أو حذفها",
                        fontSize = 11.sp,
                        color = HarvestMutedText
                    )
                }

                Surface(
                    onClick = {
                        newZoneName = ""
                        newZoneCrop = currentPlant.name
                        newZoneArea = "25.0"
                        showAddZoneDialog = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = HarvestPillBg,
                    border = BorderStroke(1.dp, HarvestBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة صوبة جديدة",
                            tint = HarvestPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إضافة منطقة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Zones List with Long-press Context Menu & Actions
        items(zones) { zone ->
            ZoneItemCard(
                zone = zone,
                isSelected = currentPlant.zone.contains(zone.name) || zone.name.contains(currentPlant.zone),
                onClick = { onSelectZone(zone) },
                onLongClick = {
                    contextMenuZone = zone
                },
                onOpenOptions = {
                    contextMenuZone = zone
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Context Menu Dialog (Triggered on Long Press or Options Icon)
    if (contextMenuZone != null) {
        val targetZone = contextMenuZone!!
        AlertDialog(
            onDismissRequest = { contextMenuZone = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = targetZone.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF121212)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "المحصول: ${targetZone.activePlant} • المساحة: ${targetZone.areaSqm} م²",
                        fontSize = 12.sp,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 1: Rename Zone
                    Surface(
                        onClick = {
                            val z = targetZone
                            contextMenuZone = null
                            renameInputText = z.name
                            zoneToRename = z
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = HarvestBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("menu_option_rename_zone")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "تعديل الاسم",
                                tint = HarvestPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "تعديل الاسم (Edit Name)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF121212)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Delete Zone
                    Surface(
                        onClick = {
                            val z = targetZone
                            contextMenuZone = null
                            zoneToDelete = z
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("menu_option_delete_zone")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف المنطقة",
                                tint = HarvestAccentRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "حذف المنطقة (Delete)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestAccentRed
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { contextMenuZone = null }
                ) {
                    Text(
                        text = "إغلاق",
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Rename Zone Dialog with High-Contrast Dark Text
    if (zoneToRename != null) {
        val targetZone = zoneToRename!!
        AlertDialog(
            onDismissRequest = { zoneToRename = null },
            title = {
                Text(
                    text = "تعديل اسم المنطقة / الصوبة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "أدخل الاسم الجديد للمنطقة:",
                        fontSize = 12.sp,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = renameInputText,
                        onValueChange = { renameInputText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFF121212),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF121212),
                            unfocusedTextColor = Color(0xFF121212),
                            focusedBorderColor = HarvestPrimary,
                            unfocusedBorderColor = HarvestBorder,
                            cursorColor = HarvestPrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rename_zone_textfield")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameInputText.isNotBlank()) {
                            onRenameZone(targetZone.id, renameInputText.trim())
                        }
                        zoneToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HarvestPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_rename_zone_btn")
                ) {
                    Text(text = "حفظ التعديل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { zoneToRename = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "إلغاء", color = Color(0xFF424242))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete Confirmation Dialog (Cascade Deletion Warning)
    if (zoneToDelete != null) {
        val targetZone = zoneToDelete!!
        AlertDialog(
            onDismissRequest = { zoneToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف المنطقة",
                    tint = HarvestAccentRed,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "تأكيد حذف الصوبة / المنطقة",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "هل أنت متأكد من رغبتك في حذف '${targetZone.name}'؟",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF121212),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "⚠️ تحذير الحذف المتسلسل (Cascade Deletion):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "حذف هذه الصوبة سيؤدي أيضاً إلى حذف جميع النباتات والسنسورات المرتبطة بها.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFB71C1C),
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Deleting this zone will also remove all plants and sensors inside it.",
                                fontSize = 11.sp,
                                color = Color(0xFF5A1A1A),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteZone(targetZone.id)
                        zoneToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HarvestAccentRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_delete_zone_btn")
                ) {
                    Text(text = "حذف متسلسل نهائياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { zoneToDelete = null },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("cancel_delete_zone_btn")
                ) {
                    Text(text = "إلغاء", color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Add New Zone Dialog
    if (showAddZoneDialog) {
        AlertDialog(
            onDismissRequest = { showAddZoneDialog = false },
            title = {
                Text(
                    text = "إضافة منطقة / صوبة جديدة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "اسم المنطقة / الصوبة:", fontSize = 12.sp, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newZoneName,
                        onValueChange = { newZoneName = it },
                        placeholder = { Text("مثال: المنطقة ٥ - صوبة الفلفل", color = Color(0xFF757575)) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color(0xFF121212), fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF121212),
                            unfocusedTextColor = Color(0xFF121212),
                            focusedBorderColor = HarvestPrimary,
                            unfocusedBorderColor = HarvestBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "المحصول المزروع بها:", fontSize = 12.sp, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newZoneCrop,
                        onValueChange = { newZoneCrop = it },
                        placeholder = { Text("مثال: فلفل رومي", color = Color(0xFF757575)) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color(0xFF121212), fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF121212),
                            unfocusedTextColor = Color(0xFF121212),
                            focusedBorderColor = HarvestPrimary,
                            unfocusedBorderColor = HarvestBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "المساحة (م²):", fontSize = 12.sp, color = Color(0xFF424242), fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newZoneArea,
                        onValueChange = { newZoneArea = it },
                        placeholder = { Text("25.0", color = Color(0xFF757575)) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color(0xFF121212), fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF121212),
                            unfocusedTextColor = Color(0xFF121212),
                            focusedBorderColor = HarvestPrimary,
                            unfocusedBorderColor = HarvestBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newZoneName.isNotBlank()) {
                            val area = newZoneArea.toDoubleOrNull() ?: 25.0
                            onAddZone(newZoneName.trim(), if (newZoneCrop.isNotBlank()) newZoneCrop.trim() else "محصول متنوع", area)
                        }
                        showAddZoneDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HarvestPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "إضافة المنطقة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddZoneDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "إلغاء", color = Color(0xFF424242))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun CropItemCard(
    crop: PlantConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onConsultAi: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.95f)
        ),
        border = if (isSelected) BorderStroke(2.dp, HarvestPrimary) else BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("crop_item_${crop.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) HarvestPrimary else HarvestBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Eco,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else HarvestPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = crop.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarvestPrimary
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = HarvestPillBg,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, HarvestBorder)
                            ) {
                                Text(
                                    text = "نشط",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "العتبة: ${crop.moistureThreshold}% • ${crop.zone}",
                        fontSize = 11.sp,
                        color = HarvestMutedText
                    )
                }
            }

            Surface(
                onClick = onConsultAi,
                shape = RoundedCornerShape(10.dp),
                color = HarvestPillBg,
                border = BorderStroke(1.dp, HarvestBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "استشارة AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarvestPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoneItemCard(
    zone: ZoneInfo,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onOpenOptions: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.95f)
        ),
        border = if (isSelected) BorderStroke(1.5.dp, HarvestPrimary) else BorderStroke(1.dp, HarvestBorder.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("zone_card_${zone.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) HarvestPrimary.copy(alpha = 0.15f) else HarvestBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = HarvestPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = zone.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF121212)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(التركيز الحالي)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarvestPrimary
                            )
                        }
                    }
                    Text(
                        text = "${zone.activePlant} • ${zone.areaSqm} م² • رطوبة: ${zone.currentMoisture}%",
                        fontSize = 11.sp,
                        color = Color(0xFF424242)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (zone.valveStatus == "OPEN") Color(0xFFE8F5E9) else HarvestPillBg,
                    border = BorderStroke(1.dp, if (zone.valveStatus == "OPEN") Color(0xFF2E7D32) else HarvestBorder)
                ) {
                    Text(
                        text = if (zone.valveStatus == "OPEN") "الصمام مفتوح" else "صمام مغلق",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (zone.valveStatus == "OPEN") Color(0xFF2E7D32) else HarvestPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onOpenOptions,
                    modifier = Modifier.size(32.dp).testTag("zone_options_btn_${zone.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "خيارات المنطقة",
                        tint = HarvestMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
