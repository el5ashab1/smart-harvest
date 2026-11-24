package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.location.LocationService
import com.example.ui.SmartHarvestViewModel
import com.example.ui.components.AddCropAiBottomSheet
import com.example.ui.components.AiAdviceBottomSheet
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlantsZonesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.HarvestBackground
import com.example.ui.theme.HarvestPrimary
import com.example.ui.theme.HarvestSecondary
import com.example.ui.theme.SmartHarvestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: SmartHarvestViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartHarvestTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    SmartHarvestApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SmartHarvestApp(viewModel: SmartHarvestViewModel) {
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }

    // Dynamic Runtime Location Permissions Handling
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // Request permissions and evaluate live weather with accurate coordinates or Giza default
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        val locationQuery = locationService.getFormattedLocationQuery()
        viewModel.setLocationQuery(locationQuery)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAiBottomSheet by remember { mutableStateOf(false) }
    var showAddCropAiBottomSheet by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addCropSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val currentPlant by viewModel.currentPlant.collectAsStateWithLifecycle()
    val savedCrops by viewModel.savedCrops.collectAsStateWithLifecycle()
    val sensorData by viewModel.sensorData.collectAsStateWithLifecycle()
    val control by viewModel.systemControl.collectAsStateWithLifecycle()
    val wateringLogs by viewModel.wateringLogs.collectAsStateWithLifecycle()
    val zones by viewModel.zones.collectAsStateWithLifecycle()
    val isEspConnected by viewModel.isEsp32Connected.collectAsStateWithLifecycle()
    val isSimulationActive by viewModel.isSimulationActive.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val lowWaterAlertEnabled by viewModel.lowWaterAlertEnabled.collectAsStateWithLifecycle()
    val aiAdvice by viewModel.aiAdvice.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val liveWeather by viewModel.liveWeather.collectAsStateWithLifecycle()
    val cropProfile by viewModel.cropAgronomicProfile.collectAsStateWithLifecycle()
    val smartAdvisory by viewModel.smartFarmingAdvisory.collectAsStateWithLifecycle()
    val isEvaluatingAdvisory by viewModel.isEvaluatingAdvisory.collectAsStateWithLifecycle()

    Scaffold(

        modifier = Modifier.fillMaxSize(),
        containerColor = HarvestBackground,
        bottomBar = {
            Surface(
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.HarvestBorder),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    // Tab 0: Home (الرئيسية)
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "الرئيسية"
                            )
                        },
                        label = {
                            Text(
                                text = "الرئيسية",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HarvestPrimary,
                            selectedTextColor = HarvestPrimary,
                            indicatorColor = HarvestBackground,
                            unselectedIconColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f),
                            unselectedTextColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    // Tab 1: Analytics (التحليلات)
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                contentDescription = "التحليلات"
                            )
                        },
                        label = {
                            Text(
                                text = "التحليلات",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HarvestPrimary,
                            selectedTextColor = HarvestPrimary,
                            indicatorColor = HarvestBackground,
                            unselectedIconColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f),
                            unselectedTextColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_tab_analytics")
                    )

                    // Tab 2: Plants & Zones (المحاصيل)
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Eco else Icons.Outlined.Eco,
                                contentDescription = "المحاصيل"
                            )
                        },
                        label = {
                            Text(
                                text = "المحاصيل",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 2) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HarvestPrimary,
                            selectedTextColor = HarvestPrimary,
                            indicatorColor = HarvestBackground,
                            unselectedIconColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f),
                            unselectedTextColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_tab_plants")
                    )

                    // Tab 3: Settings (الإعدادات)
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "الإعدادات"
                            )
                        },
                        label = {
                            Text(
                                text = "الإعدادات",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 3) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HarvestPrimary,
                            selectedTextColor = HarvestPrimary,
                            indicatorColor = HarvestBackground,
                            unselectedIconColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f),
                            unselectedTextColor = com.example.ui.theme.HarvestMutedText.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeScreen(
                plant = currentPlant,
                sensorData = sensorData,
                control = control,
                isEspConnected = isEspConnected,
                liveWeather = liveWeather,
                cropProfile = cropProfile,
                advisory = smartAdvisory,
                isEvaluatingAdvisory = isEvaluatingAdvisory,
                zones = zones,
                onSelectZone = { selectedZone ->
                    viewModel.selectActiveZone(selectedZone.id)
                },
                onForceIrrigateZone = { targetZone, turnOn ->
                    viewModel.forceIrrigateZone(targetZone.id, turnOn)
                },
                onRefreshEvaluation = {
                    coroutineScope.launch {
                        val locQuery = locationService.getFormattedLocationQuery()
                        viewModel.setLocationQuery(locQuery)
                    }
                },
                onToggleManualOverride = { viewModel.toggleManualOverride(it) },
                onTogglePump = { viewModel.setPumpState(it) },
                onOpenAiAdvice = {
                    viewModel.requestAiAdvice(currentPlant.name)
                    showAiBottomSheet = true
                },
                modifier = Modifier.padding(innerPadding)
            )

            1 -> AnalyticsScreen(
                wateringLogs = wateringLogs,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> PlantsZonesScreen(
                currentPlant = currentPlant,
                savedCrops = savedCrops,
                zones = zones,
                onSelectPlant = { viewModel.switchActivePlant(it) },
                onSelectZone = { selectedZone ->
                    viewModel.selectActiveZone(selectedZone.id)
                },
                onRenameZone = { zoneId, newName ->
                    viewModel.renameZone(zoneId, newName)
                },
                onDeleteZone = { zoneId ->
                    viewModel.deleteZone(zoneId)
                },
                onAddZone = { name, activePlant, areaSqm ->
                    viewModel.addZone(name, activePlant, areaSqm)
                },
                onUpdateThreshold = { viewModel.updatePlantThreshold(it) },
                onRequestAiAdvice = { plantName ->
                    viewModel.requestAiAdvice(plantName)
                    showAiBottomSheet = true
                },
                onOpenAddCropAi = {
                    showAddCropAiBottomSheet = true
                },
                modifier = Modifier.padding(innerPadding)
            )
            3 -> SettingsScreen(
                isEspConnected = isEspConnected,
                isSimulationActive = isSimulationActive,
                notificationsEnabled = notificationsEnabled,
                lowWaterAlertEnabled = lowWaterAlertEnabled,
                onToggleSimulation = { viewModel.toggleSimulation(it) },
                onToggleNotifications = { viewModel.toggleNotifications(it) },
                onToggleLowWaterAlert = { viewModel.toggleLowWaterAlert(it) },
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (showAiBottomSheet) {
            AiAdviceBottomSheet(
                advice = aiAdvice,
                isLoading = isAiLoading,
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        showAiBottomSheet = false
                    }
                },
                onApplyOptimalThreshold = { optimalThreshold ->
                    viewModel.updatePlantThreshold(optimalThreshold)
                },
                sheetState = sheetState
            )
        }

        if (showAddCropAiBottomSheet) {
            AddCropAiBottomSheet(
                onDismiss = {
                    coroutineScope.launch { addCropSheetState.hide() }.invokeOnCompletion {
                        showAddCropAiBottomSheet = false
                    }
                },
                onFetchAiCareInfo = { plantName ->
                    viewModel.fetchPlantCareInfo(plantName)
                },
                onConfirmAndSetActiveCrop = { name, threshold, zone, maxMoist, duration, sun, tips ->
                    viewModel.saveAndSetActiveCrop(
                        plantName = name,
                        moistureThreshold = threshold,
                        zoneName = zone,
                        targetMaxMoisture = maxMoist,
                        wateringDurationMins = duration,
                        sunlightNeeds = sun,
                        careTips = tips
                    )
                },
                sheetState = addCropSheetState
            )
        }
    }
}
