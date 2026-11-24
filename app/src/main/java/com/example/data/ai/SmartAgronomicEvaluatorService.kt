package com.example.data.ai

import com.example.data.models.AdvisorySeverity
import com.example.data.models.CropAgronomicProfile
import com.example.data.models.LiveWeatherData
import com.example.data.models.SmartFarmingAdvisory

/**
 * Smart Evaluation Service that cross-references live weather telemetry against
 * Gemini AI's structured agronomic crop profile to compute dynamic, actionable farming advisories.
 */
class SmartAgronomicEvaluatorService {

    /**
     * Cross-evaluates live weather telemetry against plant agronomic conditions
     */
    fun evaluate(
        weather: LiveWeatherData,
        cropProfile: CropAgronomicProfile
    ): SmartFarmingAdvisory {
        val tempDiffAboveMax = weather.tempC - cropProfile.idealTempMax
        val tempDiffBelowMin = cropProfile.idealTempMin - weather.tempC

        // 1. Determine Thermal Stress & Severity
        val severity: AdvisorySeverity
        val tempStatus: String
        val headline: String
        var recommendedMoistureThreshold = cropProfile.idealSoilMoistureMin
        var moistureAdjustmentText = "الحفاظ على عتبة الري عند $recommendedMoistureThreshold%"

        when {
            tempDiffAboveMax >= 6.0 -> {
                severity = AdvisorySeverity.CRITICAL
                tempStatus = "إجهاد حراري حاد (+${String.format("%.1f", tempDiffAboveMax)}°C فوق الحد الأقصى ${cropProfile.idealTempMax}°C)"
                headline = "تحذير موجة حر شديدة: رفع دورات الري وتبريد المحصول فوراً"
                recommendedMoistureThreshold = (cropProfile.idealSoilMoistureMin + 12).coerceAtMost(cropProfile.idealSoilMoistureMax)
                moistureAdjustmentText = "رفع عتبة تشغيل الري إلى $recommendedMoistureThreshold% لمنع جفاف الجذور وتثبيط النتح"
            }
            tempDiffAboveMax > 0.5 -> {
                severity = AdvisorySeverity.WARNING
                tempStatus = "حرارة مرتفعة (+${String.format("%.1f", tempDiffAboveMax)}°C أعلى من المثالي ${cropProfile.idealTempMax}°C)"
                headline = "ارتفاع درجات الحرارة: زيادة وتيرة الري مطلوبة"
                recommendedMoistureThreshold = (cropProfile.idealSoilMoistureMin + 6).coerceAtMost(cropProfile.idealSoilMoistureMax)
                moistureAdjustmentText = "تعديل عتبة الري إلى $recommendedMoistureThreshold% لتعويض سرعة التبخر السطحي"
            }
            tempDiffBelowMin >= 5.0 -> {
                severity = AdvisorySeverity.WARNING
                tempStatus = "انخفاض حاد في الحرارة (-${String.format("%.1f", tempDiffBelowMin)}°C دون الحد الأدنى ${cropProfile.idealTempMin}°C)"
                headline = "تنبيه طقس بارد: تقليل كميات المياه لتجنب تعفن الجذور"
                recommendedMoistureThreshold = (cropProfile.idealSoilMoistureMin - 5).coerceAtLeast(25)
                moistureAdjustmentText = "خفض عتبة الري إلى $recommendedMoistureThreshold% وتجنب الري المسائي"
            }
            tempDiffBelowMin > 0.5 -> {
                severity = AdvisorySeverity.ATTENTION
                tempStatus = "طقس مائل للبرودة (${weather.tempC}°C أقل من ${cropProfile.idealTempMin}°C)"
                headline = "انتباه: تباطؤ امتصاص الماء بسبب برودة الجو"
                recommendedMoistureThreshold = cropProfile.idealSoilMoistureMin
                moistureAdjustmentText = "الحفاظ على الري في الظهيرة مع عتبة $recommendedMoistureThreshold%"
            }
            else -> {
                severity = AdvisorySeverity.OPTIMAL
                tempStatus = "الحرارة مثالية (${weather.tempC}°C ضمن نطاق ${cropProfile.idealTempMin}°C - ${cropProfile.idealTempMax}°C)"
                headline = "الظروف المناخية مثالية لنمو ${cropProfile.cropName}"
                recommendedMoistureThreshold = cropProfile.idealSoilMoistureMin
                moistureAdjustmentText = "عتبة الري الافتراضية ($recommendedMoistureThreshold%) مثالية ومستقرة"
            }
        }

        // 2. Compute Evaporation Risk & Humidity Status
        val humidityStatus = if (weather.humidity < 40) {
            "رطوبة جوية منخفضة (${weather.humidity}%) - جفاف هوائي"
        } else if (weather.humidity > 80) {
            "رطوبة جوية مرتفعة جداً (${weather.humidity}%) - قابلية للفطريات"
        } else {
            "رطوبة جوية متوازنة (${weather.humidity}%)"
        }

        val evaporationRisk = when {
            weather.tempC >= 32.0 && weather.humidity <= 40 -> "شديد جداً"
            weather.tempC >= 28.0 || weather.windKph >= 20.0 -> "مرتفع"
            weather.humidity >= 70 -> "منخفض"
            else -> "معتدل"
        }

        // 3. Build Actionable Advisory Guidance Steps
        val actionableSteps = mutableListOf<String>()

        if (severity == AdvisorySeverity.CRITICAL || severity == AdvisorySeverity.WARNING) {
            if (tempDiffAboveMax > 0) {
                actionableSteps.add("جدولة دورة ري إضافية صباحية مبكرة قبل ذروة سطوع الشمس.")
                actionableSteps.add("تشغيل مراوح التهوية وتفعيل شبك التظليل لخفض الإشعاع المباشر.")
            } else if (tempDiffBelowMin > 0) {
                actionableSteps.add("إغلاق فتحات التهوية بالصوبات ليلاً للحفاظ على الدفء الداخلي.")
                actionableSteps.add("تأجيل الري إلى ساعات منتصف النهار الدافئة.")
            }
        } else {
            actionableSteps.add("استمرار نظام الري الذكي التلقائي وفق عتبة $recommendedMoistureThreshold%.")
            actionableSteps.add("فحص قراءات حساس تدفق المياه ومستوى الخزان دورياً.")
        }

        actionableSteps.add(cropProfile.wateringScheduleTips)

        return SmartFarmingAdvisory(
            id = "adv_${System.currentTimeMillis()}",
            cropName = cropProfile.cropName,
            timestamp = System.currentTimeMillis(),
            severity = severity,
            headline = headline,
            moistureAdjustmentRecommendation = moistureAdjustmentText,
            recommendedMoistureThreshold = recommendedMoistureThreshold,
            temperatureStressStatus = tempStatus,
            humidityStatus = humidityStatus,
            evaporationRisk = evaporationRisk,
            actionableSteps = actionableSteps,
            weatherSnapshot = weather,
            cropProfileSnapshot = cropProfile
        )
    }
}
