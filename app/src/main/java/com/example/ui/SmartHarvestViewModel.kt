package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.SmartHarvestRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SmartHarvestViewModel(
    private val repository: SmartHarvestRepository = SmartHarvestRepository()
) : ViewModel() {


    val currentPlant: StateFlow<PlantConfig> = repository.currentPlant.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.currentPlant.value
    )

    val sensorData: StateFlow<SensorData> = repository.sensorData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.sensorData.value
    )

    val systemControl: StateFlow<SystemControl> = repository.systemControl.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.systemControl.value
    )

    val wateringLogs: StateFlow<List<WateringLog>> = repository.wateringLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.wateringLogs.value
    )

    val zones: StateFlow<List<ZoneInfo>> = repository.zones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.zones.value
    )

    val savedCrops: StateFlow<List<PlantConfig>> = repository.savedCrops.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.savedCrops.value
    )

    val isEsp32Connected: StateFlow<Boolean> = repository.isEsp32Connected.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.isEsp32Connected.value
    )

    val isSimulationActive: StateFlow<Boolean> = repository.isSimulationActive.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.isSimulationActive.value
    )

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.notificationsEnabled.value
    )

    val lowWaterAlertEnabled: StateFlow<Boolean> = repository.lowWaterAlertEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.lowWaterAlertEnabled.value
    )

    val aiAdvice: StateFlow<AiAgronomicAdvice?> = repository.aiAdvice.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isAiLoading: StateFlow<Boolean> = repository.isAiLoading.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val currentLocationQuery: StateFlow<String> = repository.currentLocationQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.currentLocationQuery.value
    )

    val liveWeather: StateFlow<LiveWeatherData?> = repository.liveWeather.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.liveWeather.value
    )

    val cropAgronomicProfile: StateFlow<CropAgronomicProfile?> = repository.cropAgronomicProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.cropAgronomicProfile.value
    )

    val smartFarmingAdvisory: StateFlow<SmartFarmingAdvisory?> = repository.smartFarmingAdvisory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.smartFarmingAdvisory.value
    )

    val isEvaluatingAdvisory: StateFlow<Boolean> = repository.isEvaluatingAdvisory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setLocationQuery(query: String) {
        repository.setLocationQuery(query)
    }

    fun evaluateCropWithLiveWeather(cropName: String = currentPlant.value.name, locationQuery: String? = null) {
        repository.evaluateCropWithLiveWeather(cropName, locationQuery)
    }

    fun renameZone(zoneId: String, newName: String) {
        repository.renameZone(zoneId, newName)
    }

    fun deleteZone(zoneId: String) {
        repository.deleteZone(zoneId)
    }

    fun addZone(name: String, activePlant: String, areaSqm: Double = 25.0, moistureThreshold: Int = 50) {
        repository.addZone(name, activePlant, areaSqm, moistureThreshold)
    }

    fun selectActiveZone(zoneId: String) {
        repository.selectActiveZone(zoneId)
    }

    fun forceIrrigateZone(zoneId: String, turnOn: Boolean = true) {
        repository.forceIrrigateZone(zoneId, turnOn)
    }

    fun toggleManualOverride(enable: Boolean) {

        repository.toggleManualOverride(enable)
    }

    fun setPumpState(turnOn: Boolean) {
        repository.setPumpState(turnOn, isManual = true)
    }

    fun updatePlantThreshold(newThreshold: Int) {
        repository.updatePlantThreshold(newThreshold)
    }

    fun switchActivePlant(plant: PlantConfig) {
        repository.switchActivePlant(plant)
    }

    fun saveAndSetActiveCrop(
        plantName: String,
        moistureThreshold: Int,
        zoneName: String = "المنطقة الأولى",
        targetMaxMoisture: Int = 75,
        wateringDurationMins: Int = 10,
        sunlightNeeds: String = "شمس كاملة",
        careTips: String = "ري منتظم مع الحفاظ على تهوية التربة"
    ) {
        repository.saveAndSetActiveCrop(
            plantName = plantName,
            moistureThreshold = moistureThreshold,
            zoneName = zoneName,
            targetMaxMoisture = targetMaxMoisture,
            wateringDurationMins = wateringDurationMins,
            sunlightNeeds = sunlightNeeds,
            careTips = careTips
        )
    }

    suspend fun fetchPlantCareInfo(plantName: String): AiCropCareInfo {
        return repository.fetchPlantCareInfo(plantName)
    }

    fun requestAiAdvice(plantName: String? = null) {
        repository.requestAiAdvice(plantName)
    }

    fun toggleSimulation(active: Boolean) {
        repository.toggleSimulation(active)
    }

    fun toggleNotifications(enabled: Boolean) {
        repository.toggleNotifications(enabled)
    }

    fun toggleLowWaterAlert(enabled: Boolean) {
        repository.toggleLowWaterAlert(enabled)
    }
}
