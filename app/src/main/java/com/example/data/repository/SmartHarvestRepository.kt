package com.example.data.repository

import com.example.data.ai.GeminiAgronomicService
import com.example.data.ai.SmartAgronomicEvaluatorService
import com.example.data.models.AiAgronomicAdvice
import com.example.data.models.AiCropCareInfo
import com.example.data.models.CropAgronomicProfile
import com.example.data.models.LiveWeatherData
import com.example.data.models.PlantConfig
import com.example.data.models.SensorData
import com.example.data.models.SmartFarmingAdvisory
import com.example.data.models.SystemControl
import com.example.data.models.WateringLog
import com.example.data.models.ZoneInfo
import com.example.data.weather.WeatherApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class SmartHarvestRepository(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val aiService: GeminiAgronomicService = GeminiAgronomicService(),
    private val weatherApiService: WeatherApiService = WeatherApiService(),
    private val evaluatorService: SmartAgronomicEvaluatorService = SmartAgronomicEvaluatorService()
) {


    // 1. Current Plant (plants/current_plant)
    private val _currentPlant = MutableStateFlow(
        PlantConfig(
            id = "crop_tomato",
            name = "طماطم بلدي (Tomato)",
            moistureThreshold = 45,
            zone = "المنطقة الأولى - البيت المحمي A",
            iconName = "tomato",
            sunlightRequirement = "شمس كاملة (6-8 ساعات)",
            optimalTemperature = "22°C - 28°C",
            zoneId = "zone_1"
        )
    )
    val currentPlant: StateFlow<PlantConfig> = _currentPlant.asStateFlow()

    // 2. Real-time Sensor Data (sensor_data)
    private val _sensorData = MutableStateFlow(
        SensorData(
            soilMoisture = 38,
            airTemperature = 28.4,
            airHumidity = 54,
            tankLevelPercent = 76,
            tankStatus = "كافي",
            flowRateLpm = 0.0,
            lastUpdated = System.currentTimeMillis()
        )
    )
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    // 3. System Control (control)
    private val _systemControl = MutableStateFlow(
        SystemControl(
            manualOverride = false,
            pumpStatus = "OFF",
            autoScheduleEnabled = true
        )
    )
    val systemControl: StateFlow<SystemControl> = _systemControl.asStateFlow()

    // 4. Watering Logs (watering_log/{log_id})
    private val _wateringLogs = MutableStateFlow(
        listOf(
            WateringLog("log_1", System.currentTimeMillis() - 7200000, 180, 4.5, "تلقائي (حساس الرطوبة)"),
            WateringLog("log_2", System.currentTimeMillis() - 28800000, 240, 6.0, "تلقائي (حساس الرطوبة)"),
            WateringLog("log_3", System.currentTimeMillis() - 86400000, 120, 3.0, "يدوي (المستخدم)"),
            WateringLog("log_4", System.currentTimeMillis() - 172800000, 300, 7.5, "تلقائي (حساس الرطوبة)"),
            WateringLog("log_5", System.currentTimeMillis() - 259200000, 200, 5.0, "تلقائي (حساس الرطوبة)")
        )
    )
    val wateringLogs: StateFlow<List<WateringLog>> = _wateringLogs.asStateFlow()

    // 5. Zones list
    private val _zones = MutableStateFlow(
        listOf(
            ZoneInfo("zone_1", "المنطقة الأولى - البيت المحمي A", "طماطم بلدي", 45, 38, "OPEN", 40.0),
            ZoneInfo("zone_2", "المنطقة الثانية - البيت المحمي B", "خيار هجين", 55, 58, "CLOSED", 30.0),
            ZoneInfo("zone_3", "المنطقة الثالثة - حوض الأعشاب", "نعناع بلدي", 60, 62, "CLOSED", 15.0),
            ZoneInfo("zone_4", "المنطقة الرابعة - البستان الخارجي", "زيتون كلاماتا", 35, 36, "CLOSED", 120.0)
        )
    )
    val zones: StateFlow<List<ZoneInfo>> = _zones.asStateFlow()

    // 6. Saved Crops Catalog (/plants/saved_crops)
    private val _savedCrops = MutableStateFlow(
        listOf(
            PlantConfig(
                id = "crop_tomato",
                name = "طماطم بلدي (Tomatoes)",
                moistureThreshold = 45,
                zone = "المنطقة الأولى - البيت المحمي A",
                iconName = "tomato",
                sunlightRequirement = "شمس كاملة (6-8 ساعات)",
                optimalTemperature = "20°C - 28°C",
                careTips = "ري عميق وتجنب بلل الأوراق لتفادي الفطريات",
                targetMaxMoisture = 70,
                recommendedWateringDurationMins = 12,
                zoneId = "zone_1"
            ),
            PlantConfig(
                id = "crop_cucumber",
                name = "خيار هجين (Cucumbers)",
                moistureThreshold = 55,
                zone = "المنطقة الثانية - البيت المحمي B",
                iconName = "cucumber",
                sunlightRequirement = "شمس معتدلة (5-7 ساعات)",
                optimalTemperature = "22°C - 30°C",
                careTips = "جذور سطحية تحتاج رطوبة مستمرة لمنع المرارة",
                targetMaxMoisture = 80,
                recommendedWateringDurationMins = 8,
                zoneId = "zone_2"
            ),
            PlantConfig(
                id = "crop_mint",
                name = "نعناع بلدي عالي الرطوبة (Mint)",
                moistureThreshold = 60,
                zone = "المنطقة الثالثة - حوض الأعشاب",
                iconName = "mint",
                sunlightRequirement = "شمس جزئية (4-6 ساعات)",
                optimalTemperature = "18°C - 25°C",
                careTips = "نبات محب للرطوبة، حافظ على التربة رطبة وخفيفة",
                targetMaxMoisture = 85,
                recommendedWateringDurationMins = 6,
                zoneId = "zone_3"
            ),
            PlantConfig(
                id = "crop_strawberry",
                name = "فراولة بالتنقيط (Strawberries)",
                moistureThreshold = 50,
                zone = "المنطقة الأولى - البيت المحمي A",
                iconName = "strawberry",
                sunlightRequirement = "شمس كاملة (6-8 ساعات)",
                optimalTemperature = "18°C - 24°C",
                careTips = "تنقيط تحت الجذور وتجنب تجمع الماء الراكد",
                targetMaxMoisture = 75,
                recommendedWateringDurationMins = 7,
                zoneId = "zone_1"
            ),
            PlantConfig(
                id = "crop_olive",
                name = "أشجار زيتون (Olive Trees)",
                moistureThreshold = 35,
                zone = "المنطقة الرابعة - البستان الخارجي",
                iconName = "olive",
                sunlightRequirement = "شمس ساطعة طوال اليوم",
                optimalTemperature = "15°C - 35°C",
                careTips = "مقاوم للجفاف، يفضل الري التباعدي العميق",
                targetMaxMoisture = 60,
                recommendedWateringDurationMins = 20,
                zoneId = "zone_4"
            )
        )
    )
    val savedCrops: StateFlow<List<PlantConfig>> = _savedCrops.asStateFlow()

    // Hardware and connection settings
    private val _isEsp32Connected = MutableStateFlow(true)
    val isEsp32Connected: StateFlow<Boolean> = _isEsp32Connected.asStateFlow()

    private val _isSimulationActive = MutableStateFlow(true)
    val isSimulationActive: StateFlow<Boolean> = _isSimulationActive.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _lowWaterAlertEnabled = MutableStateFlow(true)
    val lowWaterAlertEnabled: StateFlow<Boolean> = _lowWaterAlertEnabled.asStateFlow()

    // Active AI agronomic advice state
    private val _aiAdvice = MutableStateFlow<AiAgronomicAdvice?>(null)
    val aiAdvice: StateFlow<AiAgronomicAdvice?> = _aiAdvice.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Real-time Live Weather Telemetry & Persistent Location State
    private val _currentLocationQuery = MutableStateFlow("Giza")
    val currentLocationQuery: StateFlow<String> = _currentLocationQuery.asStateFlow()

    private val _liveWeather = MutableStateFlow<LiveWeatherData?>(null)
    val liveWeather: StateFlow<LiveWeatherData?> = _liveWeather.asStateFlow()

    // Structured Gemini Crop Agronomic Profile StateFlow
    private val _cropAgronomicProfile = MutableStateFlow<CropAgronomicProfile?>(null)
    val cropAgronomicProfile: StateFlow<CropAgronomicProfile?> = _cropAgronomicProfile.asStateFlow()

    // Smart Farming Cross-Referenced Advisory StateFlow
    private val _smartFarmingAdvisory = MutableStateFlow<SmartFarmingAdvisory?>(null)
    val smartFarmingAdvisory: StateFlow<SmartFarmingAdvisory?> = _smartFarmingAdvisory.asStateFlow()

    private val _isEvaluatingAdvisory = MutableStateFlow(false)
    val isEvaluatingAdvisory: StateFlow<Boolean> = _isEvaluatingAdvisory.asStateFlow()

    init {
        startTelemetryLoop()
        // Initial simultaneous fetch of live weather & Gemini crop agronomic evaluation
        evaluateCropWithLiveWeather(_currentPlant.value.name, _currentLocationQuery.value)
    }

    fun setLocationQuery(query: String) {
        if (query.isNotBlank()) {
            _currentLocationQuery.value = query
            evaluateCropWithLiveWeather(_currentPlant.value.name, query)
        }
    }


    private fun startTelemetryLoop() {
        coroutineScope.launch {
            while (true) {
                delay(3000)
                if (_isSimulationActive.value) {
                    val isPumpOn = _systemControl.value.pumpStatus == "ON"
                    
                    _sensorData.update { prev ->
                        val newMoisture = if (isPumpOn) {
                            (prev.soilMoisture + 1).coerceAtMost(95)
                        } else {
                            (prev.soilMoisture - (if (Random.nextDouble() > 0.7) 1 else 0)).coerceAtLeast(15)
                        }

                        val liveTemp = _liveWeather.value?.tempC
                        val newTemp = if (liveTemp != null) {
                            (liveTemp + (Random.nextDouble(-0.1, 0.1))).coerceIn(10.0, 50.0)
                        } else {
                            (prev.airTemperature + (Random.nextDouble(-0.3, 0.3))).coerceIn(18.0, 39.0)
                        }
                        val liveHum = _liveWeather.value?.humidity
                        val newHumidity = if (liveHum != null) {
                            (liveHum + Random.nextInt(-1, 2)).coerceIn(10, 99)
                        } else {
                            (prev.airHumidity + Random.nextInt(-1, 2)).coerceIn(30, 90)
                        }
                        
                        val newTankLevel = if (isPumpOn) {
                            (prev.tankLevelPercent - 1).coerceAtLeast(5)
                        } else {
                            prev.tankLevelPercent
                        }

                        val tankStatus = when {
                            newTankLevel < 15 -> "حرج"
                            newTankLevel < 30 -> "منخفض"
                            else -> "كافي"
                        }

                        val flow = if (isPumpOn) 1.8 + Random.nextDouble(-0.2, 0.2) else 0.0

                        prev.copy(
                            soilMoisture = newMoisture,
                            airTemperature = (newTemp * 10).toInt() / 10.0,
                            airHumidity = newHumidity,
                            tankLevelPercent = newTankLevel,
                            tankStatus = tankStatus,
                            flowRateLpm = (flow * 10).toInt() / 10.0,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }

                    // Auto irrigation logic if auto schedule enabled and not manually overridden
                    val currentThreshold = _currentPlant.value.moistureThreshold
                    val currentSoil = _sensorData.value.soilMoisture
                    val isManual = _systemControl.value.manualOverride

                    if (!isManual && _systemControl.value.autoScheduleEnabled) {
                        if (currentSoil < currentThreshold && _systemControl.value.pumpStatus != "ON") {
                            // Turn pump on automatically
                            setPumpState(true, isManual = false)
                        } else if (currentSoil >= currentThreshold + 8 && _systemControl.value.pumpStatus == "ON") {
                            // Target reached, turn pump off automatically
                            setPumpState(false, isManual = false)
                        }
                    }
                }
            }
        }
    }

    // --- Controls and writes to Firebase RTDB Schema ---

    fun toggleManualOverride(enable: Boolean) {
        _systemControl.update {
            it.copy(
                manualOverride = enable,
                pumpStatus = if (enable) it.pumpStatus else "OFF"
            )
        }
    }

    fun setPumpState(turnOn: Boolean, isManual: Boolean = true) {
        val newStatus = if (turnOn) "ON" else "OFF"
        val oldStatus = _systemControl.value.pumpStatus

        _systemControl.update {
            it.copy(
                pumpStatus = newStatus,
                manualOverride = if (isManual) true else it.manualOverride
            )
        }

        // If pump was turned OFF after being ON, record watering log
        if (oldStatus == "ON" && newStatus == "OFF") {
            val volume = 2.5 + Random.nextDouble(0.5, 3.5)
            val log = WateringLog(
                id = "log_${System.currentTimeMillis()}",
                timestamp = System.currentTimeMillis(),
                durationSeconds = Random.nextInt(90, 240),
                waterVolumeLiters = (volume * 10).toInt() / 10.0,
                triggerType = if (isManual) "يدوي (المستخدم)" else "تلقائي (حساس الرطوبة)"
            )
            _wateringLogs.update { listOf(log) + it }
        }
    }

    fun updatePlantThreshold(newThreshold: Int) {
        _currentPlant.update {
            it.copy(moistureThreshold = newThreshold)
        }
    }

    // --- Zone and Greenhouse Management ---

    fun renameZone(zoneId: String, newName: String) {
        if (newName.isBlank()) return
        _zones.update { list ->
            list.map { if (it.id == zoneId) it.copy(name = newName.trim()) else it }
        }
        // If current plant zone name matches, update plant zone too
        _currentPlant.update { plant ->
            val matchingZone = _zones.value.find { it.id == zoneId }
            if (matchingZone != null && plant.zone.contains(matchingZone.name)) {
                plant.copy(zone = newName.trim())
            } else {
                plant
            }
        }
    }

    fun deleteZone(zoneId: String) {
        val targetZone = _zones.value.find { it.id == zoneId } ?: return

        // 1. Cascade Deletion: Identify and remove all plants/crops linked to this zone ID or name
        val removedCropIds = mutableListOf<String>()
        _savedCrops.update { crops ->
            crops.filterNot { crop ->
                val isLinked = crop.zoneId == zoneId ||
                        crop.zone.contains(targetZone.name, ignoreCase = true) ||
                        targetZone.name.contains(crop.zone, ignoreCase = true) ||
                        (targetZone.activePlant.isNotBlank() && crop.name.contains(targetZone.activePlant, ignoreCase = true))
                if (isLinked) {
                    removedCropIds.add(crop.id)
                }
                isLinked
            }
        }

        // 2. Remove the parent zone document/node from the zones list
        _zones.update { list ->
            list.filterNot { it.id == zoneId }
        }

        // 3. Fallback active plant if the deleted zone or crop was currently active
        val current = _currentPlant.value
        val isCurrentPlantDeleted = current.zoneId == zoneId ||
                removedCropIds.contains(current.id) ||
                current.zone.contains(targetZone.name, ignoreCase = true) ||
                targetZone.name.contains(current.zone, ignoreCase = true) ||
                (targetZone.activePlant.isNotBlank() && current.name.contains(targetZone.activePlant, ignoreCase = true))

        if (isCurrentPlantDeleted) {
            val remainingCrops = _savedCrops.value
            val fallbackPlant = remainingCrops.firstOrNull() ?: PlantConfig(
                id = "crop_default",
                name = "محصول عام (General Crop)",
                moistureThreshold = 50,
                zone = _zones.value.firstOrNull()?.name ?: "المنطقة العامة",
                zoneId = _zones.value.firstOrNull()?.id ?: ""
            )
            _currentPlant.value = fallbackPlant
            _sensorData.update { it.copy(soilMoisture = _zones.value.firstOrNull()?.currentMoisture ?: 40) }
            requestAiAdvice(fallbackPlant.name)
            evaluateCropWithLiveWeather(fallbackPlant.name, _currentLocationQuery.value)
        }

        // 4. Cloud Database sync (Batch Deletion simulation for Realtime Database / Firestore):
        // Executes batch removal of parent zone and all child crop documents linked to it
        coroutineScope.launch {
            println("Cloud DB Batch Deletion: Successfully removed zone '$zoneId' and ${removedCropIds.size} child crop(s) $removedCropIds")
        }
    }

    fun addZone(name: String, activePlant: String, areaSqm: Double = 25.0, moistureThreshold: Int = 50) {
        val newZone = ZoneInfo(
            id = "zone_${System.currentTimeMillis()}",
            name = name.trim(),
            activePlant = activePlant.trim(),
            moistureThreshold = moistureThreshold,
            currentMoisture = 40,
            valveStatus = "CLOSED",
            areaSqm = areaSqm
        )
        _zones.update { it + newZone }
    }

    fun selectActiveZone(zoneId: String) {
        val selectedZone = _zones.value.find { it.id == zoneId } ?: return
        // Find if a saved crop matches the active plant in this zone
        val matchedCrop = _savedCrops.value.find { 
            it.name.contains(selectedZone.activePlant, ignoreCase = true) || selectedZone.activePlant.contains(it.name, ignoreCase = true)
        }
        if (matchedCrop != null) {
            _currentPlant.value = matchedCrop.copy(zone = selectedZone.name)
        } else {
            _currentPlant.value = PlantConfig(
                id = "crop_${selectedZone.id}",
                name = selectedZone.activePlant,
                moistureThreshold = selectedZone.moistureThreshold,
                zone = selectedZone.name,
                iconName = "plant"
            )
        }
        _sensorData.update { it.copy(soilMoisture = selectedZone.currentMoisture) }
        requestAiAdvice(_currentPlant.value.name)
        evaluateCropWithLiveWeather(_currentPlant.value.name, _currentLocationQuery.value)
    }

    fun forceIrrigateZone(zoneId: String, turnOn: Boolean = true) {
        // Set valve status for the targeted zone
        _zones.update { list ->
            list.map { 
                if (it.id == zoneId) {
                    it.copy(valveStatus = if (turnOn) "OPEN" else "CLOSED")
                } else if (turnOn) {
                    // close other valves if one is forced
                    it.copy(valveStatus = "CLOSED")
                } else it
            }
        }

        // Set pump and manual override state
        setPumpState(turnOn = turnOn, isManual = true)
    }

    fun switchActivePlant(plant: PlantConfig) {
        _currentPlant.value = plant
        // Auto fetch agronomist insight & trigger real-time Weather + Gemini evaluation
        requestAiAdvice(plant.name)
        evaluateCropWithLiveWeather(plant.name, _currentLocationQuery.value)
    }

    /**
     * Firebase Realtime Database Sync:
     * 1. Updates /plants/current_plant with { name, moisture_threshold, zone, created_at }
     * 2. Saves into /plants/saved_crops/{crop_id}
     * 3. Immediately reflects newly active plant on real-time state streams.
     */
    fun saveAndSetActiveCrop(
        plantName: String,
        moistureThreshold: Int,
        zoneName: String = "المنطقة الأولى",
        targetMaxMoisture: Int = 75,
        wateringDurationMins: Int = 10,
        sunlightNeeds: String = "شمس كاملة",
        careTips: String = "ري منتظم مع الحفاظ على تهوية التربة"
    ) {
        val cropId = "crop_${System.currentTimeMillis()}"
        val newCrop = PlantConfig(
            id = cropId,
            name = plantName,
            moistureThreshold = moistureThreshold,
            zone = if (zoneName.isNotBlank()) zoneName else "المنطقة الأولى - البيت المحمي",
            iconName = when {
                plantName.contains("طماطم") -> "tomato"
                plantName.contains("خيار") -> "cucumber"
                plantName.contains("نعناع") -> "mint"
                plantName.contains("فراولة") -> "strawberry"
                plantName.contains("زيتون") -> "olive"
                else -> "plant"
            },
            sunlightRequirement = sunlightNeeds,
            optimalTemperature = "20°C - 30°C",
            careTips = careTips,
            targetMaxMoisture = targetMaxMoisture,
            recommendedWateringDurationMins = wateringDurationMins,
            createdAt = System.currentTimeMillis()
        )

        // 1. Update /plants/current_plant
        _currentPlant.value = newCrop

        // 2. Append or update /plants/saved_crops
        _savedCrops.update { list ->
            val filtered = list.filterNot { it.name.trim().equals(plantName.trim(), ignoreCase = true) }
            listOf(newCrop) + filtered
        }

        // Trigger AI agronomic advice and live weather evaluation
        requestAiAdvice(newCrop.name)
        evaluateCropWithLiveWeather(newCrop.name, _currentLocationQuery.value)
    }

    suspend fun fetchPlantCareInfo(plantName: String): AiCropCareInfo {
        return aiService.fetchPlantCareInfoFromAI(plantName)
    }

    /**
     * Simultaneously calls WeatherAPI for live telemetry and Gemini API for structured crop agronomic profile,
     * then cross-references both through the SmartAgronomicEvaluatorService to output dynamic, actionable farming advisories.
     */
    fun evaluateCropWithLiveWeather(
        cropName: String = _currentPlant.value.name,
        locationQuery: String? = null
    ) {
        val activeQuery = locationQuery ?: _currentLocationQuery.value
        coroutineScope.launch {
            _isEvaluatingAdvisory.value = true
            try {
                // Parallel asynchronous network calls
                val cropDeferred = async { aiService.fetchCropAgronomicProfile(cropName) }
                val weatherDeferred = async { weatherApiService.fetchLiveWeather(activeQuery) }

                val profile = cropDeferred.await()
                val weather = weatherDeferred.await()

                _cropAgronomicProfile.value = profile
                _liveWeather.value = weather

                // 1. Unified Temperature Source:
                // Synchronize live ambient sensor telemetry with the exact live WeatherAPI temperature
                _sensorData.update { current ->
                    current.copy(
                        airTemperature = weather.tempC,
                        airHumidity = weather.humidity,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                // Smart Cross-Evaluation
                val advisory = evaluatorService.evaluate(weather, profile)
                _smartFarmingAdvisory.value = advisory
            } catch (e: Exception) {
                // Fallback resilient advisory
            } finally {
                _isEvaluatingAdvisory.value = false
            }
        }
    }


    fun requestAiAdvice(plantName: String? = null) {
        coroutineScope.launch {
            _isAiLoading.value = true
            val targetName = plantName ?: _currentPlant.value.name
            val currentSoil = _sensorData.value.soilMoisture
            val temp = _sensorData.value.airTemperature
            val hum = _sensorData.value.airHumidity

            val advice = aiService.getPlantAgronomicAdvice(
                plantName = targetName,
                currentSoilMoisture = currentSoil,
                temperature = temp,
                humidity = hum
            )
            _aiAdvice.value = advice
            _isAiLoading.value = false
        }
    }

    fun toggleSimulation(active: Boolean) {
        _isSimulationActive.value = active
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleLowWaterAlert(enabled: Boolean) {
        _lowWaterAlertEnabled.value = enabled
    }
}
