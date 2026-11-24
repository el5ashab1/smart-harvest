package com.example.data.models

data class PlantConfig(
    val id: String = "plant_1",
    val name: String = "الطماطم (Tomatoes)",
    val moistureThreshold: Int = 45, // percentage
    val zone: String = "المنطقة ١ - البيت المحمي",
    val iconName: String = "tomato",
    val sunlightRequirement: String = "شمس كاملة (6-8 ساعات يومياً)",
    val optimalTemperature: String = "20°C - 28°C",
    val careTips: String = "ري منتظم مع الحفاظ على تهوية التربة",
    val targetMaxMoisture: Int = 75,
    val recommendedWateringDurationMins: Int = 10,
    val createdAt: Long = System.currentTimeMillis(),
    val zoneId: String = ""
)

data class AiCropCareInfo(
    val plantName: String,
    val moistureThreshold: Int = 50,
    val targetMaxMoisture: Int = 70,
    val recommendedWateringDurationMins: Int = 10,
    val sunlightNeeds: String = "شمس كاملة 6-8 ساعات",
    val careTips: String = "يستحسن الري في الصباح الباكر لتجنب الفطريات."
)

data class SensorData(
    val soilMoisture: Int = 42, // % (Capacitive sensor)
    val airTemperature: Double = 27.5, // °C (DHT11)
    val airHumidity: Int = 58, // % (DHT11)
    val tankLevelPercent: Int = 82, // % (Ultrasonic sensor HC-SR04)
    val tankStatus: String = "كافي", // "كافي" (Normal), "منخفض" (Low), "حرج" (Critical)
    val flowRateLpm: Double = 1.4, // L/min (YF-S201 Flow Sensor)
    val lastUpdated: Long = System.currentTimeMillis()
)

data class SystemControl(
    val manualOverride: Boolean = false,
    val pumpStatus: String = "OFF", // "ON" or "OFF"
    val autoScheduleEnabled: Boolean = true
)

data class WateringLog(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 180,
    val waterVolumeLiters: Double = 4.2,
    val triggerType: String = "تلقائي (حساس الرطوبة)" // "تلقائي (حساس الرطوبة)" or "يدوي (المستخدم)"
)

data class ZoneInfo(
    val id: String,
    val name: String,
    val activePlant: String,
    val moistureThreshold: Int,
    val currentMoisture: Int,
    val valveStatus: String = "CLOSED",
    val areaSqm: Double = 25.0
)

data class AiAgronomicAdvice(
    val plantName: String,
    val optimalMoistureThreshold: Int,
    val sunlightNeeds: String,
    val wateringFrequency: String,
    val fertilizerRecommendation: String,
    val diseaseRiskAlert: String,
    val irrigationEfficiencyScore: Int,
    val rawAdviceText: String
)

/**
 * Structured Gemini AI Profile for Crop Agronomic Conditions
 */
data class CropAgronomicProfile(
    val cropName: String,
    val idealSoilMoistureMin: Int = 45,
    val idealSoilMoistureMax: Int = 75,
    val idealTempMin: Double = 18.0,
    val idealTempMax: Double = 28.0,
    val idealHumidityRange: String = "50% - 70%",
    val wateringScheduleTips: String = "ري منتظم في الصباح الباكر مع تجنب تشبع الجذور بالمياه الراكدة.",
    val fetchedAt: Long = System.currentTimeMillis()
)

/**
 * Real-time Weather Telemetry from WeatherAPI
 */
data class LiveWeatherData(
    val locationName: String = "القاهرة (Cairo)",
    val region: String = "Cairo",
    val country: String = "مصر",
    val tempC: Double = 29.4,
    val tempF: Double = 84.9,
    val feelsLikeC: Double = 31.0,
    val humidity: Int = 45,
    val conditionText: String = "مشمس وحار",
    val conditionIconUrl: String = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
    val windKph: Double = 14.5,
    val uvIndex: Double = 7.0,
    val isDay: Boolean = true,
    val lastUpdated: String = "الآن",
    val fetchedAt: Long = System.currentTimeMillis()
)

enum class AdvisorySeverity {
    OPTIMAL,   // الظروف مثالية
    ATTENTION, // انتباه / متابعة
    WARNING,   // تحذير بيئي / تعديل الري
    CRITICAL   // إجهاد حراري أو جفاف خطير
}

/**
 * Smart Evaluation Result: Cross-referenced live weather against Gemini crop profile
 */
data class SmartFarmingAdvisory(
    val id: String = "adv_${System.currentTimeMillis()}",
    val cropName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: AdvisorySeverity = AdvisorySeverity.OPTIMAL,
    val headline: String,
    val moistureAdjustmentRecommendation: String,
    val recommendedMoistureThreshold: Int,
    val temperatureStressStatus: String,
    val humidityStatus: String,
    val evaporationRisk: String, // "منخفض", "معتدل", "مرتفع", "شديد"
    val actionableSteps: List<String>,
    val weatherSnapshot: LiveWeatherData,
    val cropProfileSnapshot: CropAgronomicProfile
)

