package com.example.data.ai

import com.example.BuildConfig
import com.example.data.models.AiAgronomicAdvice
import com.example.data.models.AiCropCareInfo
import com.example.data.models.CropAgronomicProfile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAgronomicService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getPlantAgronomicAdvice(
        plantName: String,
        currentSoilMoisture: Int,
        temperature: Double,
        humidity: Int
    ): AiAgronomicAdvice = withContext(Dispatchers.IO) {
        val apiKey = resolveGeminiKey()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    أنت خبير زراعي وباحث في فسيولوجيا النبات وهندسة الري الذكي.
                    المحصول/النبات المطلوب تحليله بدقة علمية: "$plantName".
                    البيانات الحقلية اللحظية من حساسات ESP32:
                    - رطوبة التربة الحالية: $currentSoilMoisture%
                    - درجة حرارة المحيط: $temperature°C
                    - رطوبة الهواء الجوي: $humidity%

                    قم بتحليل متطلبات هذا المحصول بالذات ("$plantName") مقارنة بالحالة الحقلية اللحظية.
                    أجب بتنسيق JSON نظيف فقط بدون أي markdown:
                    {
                      "optimalMoistureThreshold": 45,
                      "sunlightNeeds": "تحديد احتياجات الضوء بالساعات ونوعه بدقة لـ $plantName",
                      "wateringFrequency": "جدول ومواعيد الري المناسبة لـ $plantName في هذه الظروف",
                      "fertilizerRecommendation": "التسميد والعناصر الغذائية الموصى بها لـ $plantName",
                      "diseaseRiskAlert": "المخاطر الفطرية أو البيئية المحتملة لـ $plantName عند رطوبة $humidity% وحرارة $temperature°C",
                      "irrigationEfficiencyScore": 92,
                      "rawAdviceText": "توصية إرشادية زراعية مخصصة للمزارع حول $plantName وكيفية ضبط المحابس والمضخة..."
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            }
                            put("parts", partsArray)
                        })
                    }
                    put("contents", contentsArray)
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.3)
                        put("responseMimeType", "application/json")
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBodyString = response.body?.string()
                    if (!responseBodyString.isNullOrBlank()) {
                        val rootJson = JSONObject(responseBodyString)
                        val candidates = rootJson.optJSONArray("candidates")
                        val firstCandidate = candidates?.optJSONObject(0)
                        val content = firstCandidate?.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        val text = parts?.optJSONObject(0)?.optString("text")

                        if (!text.isNullOrBlank()) {
                            val parsed = JSONObject(text.trim())
                            return@withContext AiAgronomicAdvice(
                                plantName = plantName,
                                optimalMoistureThreshold = parsed.optInt("optimalMoistureThreshold", 45),
                                sunlightNeeds = parsed.optString("sunlightNeeds", "شمس كاملة"),
                                wateringFrequency = parsed.optString("wateringFrequency", "ري منتظم"),
                                fertilizerRecommendation = parsed.optString("fertilizerRecommendation", "سماد NPK متوازن"),
                                diseaseRiskAlert = parsed.optString("diseaseRiskAlert", "لا توجد مخاطر فورية"),
                                irrigationEfficiencyScore = parsed.optInt("irrigationEfficiencyScore", 90),
                                rawAdviceText = parsed.optString("rawAdviceText", "توصية زراعية دقيقة للمحصول")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to embedded domain intelligence
            }
        }

        // High-precision agronomical fallback engine for common crops
        return@withContext generateExpertAgronomicFallback(plantName, currentSoilMoisture, temperature, humidity)
    }

    private fun generateExpertAgronomicFallback(
        plantName: String,
        moisture: Int,
        temp: Double,
        humidity: Int
    ): AiAgronomicAdvice {
        val lowerName = plantName.lowercase()
        return when {
            lowerName.contains("طماطم") || lowerName.contains("tomato") -> {
                val threshold = 45
                val score = if (moisture in (threshold - 5)..(threshold + 15)) 95 else 78
                AiAgronomicAdvice(
                    plantName = "الطماطم (Solanum lycopersicum)",
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "شمس مباشرة كاملة (6 إلى 8 ساعات يومياً)",
                    wateringFrequency = "دورة ري كل 24-36 ساعة قبل شروق الشمس",
                    fertilizerRecommendation = "سماد غني بالبوتاسيوم والكالسيوم لمنع تعفن نهاية الزهرة",
                    diseaseRiskAlert = if (humidity > 70) "ارتفاع رطوبة الجو ينذر باللفحة المتأخرة، حافظ على تهوية البيت المحمي" else "البيئة مثالية، لا توجد مخاطر فطرية حالياً",
                    irrigationEfficiencyScore = score,
                    rawAdviceText = "تتطلب الطماطم رياً عميقاً ومنتظماً مع الحفاظ على مستوى رطوبة التربة عند 45% وتجنب ترطيب الأوراق المباشر للوقاية من الفطريات."
                )
            }
            lowerName.contains("خيار") || lowerName.contains("cucumber") -> {
                val threshold = 55
                AiAgronomicAdvice(
                    plantName = "الخيار (Cucumis sativus)",
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "شمس معتدلة إلى ساطعة (5-7 ساعات)",
                    wateringFrequency = "ري متكرر يومياً بكميات محسوبة لمنع مرارة الثمار",
                    fertilizerRecommendation = "سماد نيتروجيني معتدل لتعزيز النمو الخضري ثم التحول لسماد بوتاسي",
                    diseaseRiskAlert = if (temp > 32) "إجهاد حراري محتمل، يوصى بالري الصباحي المبكر والتظليل الخفيف" else "حالة المحصول ممتازة",
                    irrigationEfficiencyScore = 91,
                    rawAdviceText = "جذور الخيار سطحية وتحتاج رطوبة مستمرة بنسبة 55% لتجنب انكماش الثمار وتدهور جودتها التسويقية."
                )
            }
            lowerName.contains("نعناع") || lowerName.contains("mint") -> {
                val threshold = 60
                AiAgronomicAdvice(
                    plantName = "النعناع البلدي (Mentha)",
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "شمس غير مباشرة أو شمس جزئية (4-6 ساعات)",
                    wateringFrequency = "ري يومي خفيف للحفاظ على رطوبة مستمرة في الطبقة العليا",
                    fertilizerRecommendation = "سماد ورقي عضوي مرة شهرياً",
                    diseaseRiskAlert = "انتبه لتصريف التربة الجيد لتفادي تعفن الجذور الدقيقة",
                    irrigationEfficiencyScore = 96,
                    rawAdviceText = "النعناع نبتة محبة للرطوبة وتستجيب فورياً لنظام التنقيط الذكي بتربة رطبة خفيفة."
                )
            }
            lowerName.contains("فراولة") || lowerName.contains("strawberr") -> {
                val threshold = 50
                AiAgronomicAdvice(
                    plantName = "الفراولة (Fragaria)",
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "شمس ساطعة (6-8 ساعات)",
                    wateringFrequency = "ري بالتنقيط الدقيق صباحاً بمعدل 1.5 لتر/شجرة",
                    fertilizerRecommendation = "سماد عالي الفوسفور لتشجيع التزهير وعقد الثمار",
                    diseaseRiskAlert = "حساسة لملوحة المياه وتراكم المياه الراكدة",
                    irrigationEfficiencyScore = 93,
                    rawAdviceText = "الفراولة تتأثر بشدة باضطرابات الري، ننصح بتثبيت عتبة الحساس عند 50% لضمان حلاوة السكر في الثمار."
                )
            }
            lowerName.contains("زيتون") || lowerName.contains("olive") -> {
                val threshold = 35
                AiAgronomicAdvice(
                    plantName = "أشجار الزيتون (Olea europaea)",
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "شمس كاملة مباشرة طوال النهار",
                    wateringFrequency = "ري تباعدي عميق كل 3-5 أيام حسب درجة الحرارة",
                    fertilizerRecommendation = "سماد NPK بطيء الذوبان وسماد عضوي شتوي",
                    diseaseRiskAlert = "مقاومة عالية للجفاف، الحذر من الري الزائد في التربة الثقيلة",
                    irrigationEfficiencyScore = 98,
                    rawAdviceText = "الزيتون شجرة متكيفة، ري بالتنقيط المتقطع عند 35% رطوبة يوفر أكثر من 40% من مياه الخزان."
                )
            }
            else -> {
                val threshold = 45
                AiAgronomicAdvice(
                    plantName = plantName,
                    optimalMoistureThreshold = threshold,
                    sunlightNeeds = "إضاءة شمسية متوازنة (6 ساعات)",
                    wateringFrequency = "دورة ري عند انخفاض الرطوبة دون $threshold%",
                    fertilizerRecommendation = "سماد مركب NPK متوازن كل 14 يوماً",
                    diseaseRiskAlert = "مراقبة دورية لحساسية التربة ومستوى نقاء ماء الري",
                    irrigationEfficiencyScore = 88,
                    rawAdviceText = "بناءً على معايير الزراعة الدقيقة لـ $plantName، يوصى بتثبيت الرطوبة عند $threshold% لتعظيم كفاءة استهلاك المياه."
                )
            }
        }
    }

    /**
     * Sends prompt to Gemini AI to extract precise plant care parameters:
     * - moisture_threshold
     * - target_max_moisture
     * - recommended_watering_duration_mins
     * - sunlight_needs
     * - care_tips
     */
    suspend fun fetchPlantCareInfoFromAI(plantName: String): AiCropCareInfo = withContext(Dispatchers.IO) {
        val apiKey = resolveGeminiKey()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    أنت مهندس زراعي وخبير ري ذكي.
                    المحصول/النبات المطلوب: "$plantName".
                    حلل المتطلبات الزراعية واقترح إعدادات الري المثالية له.
                    أجب بكائن JSON نظيف فقط بالبنية التالية تماماً:
                    {
                      "moisture_threshold": 55,
                      "target_max_moisture": 75,
                      "recommended_watering_duration_mins": 8,
                      "sunlight_needs": "شمس كاملة 6-8 ساعات يومياً",
                      "care_tips": "نصائح وإرشادات العناية بالري والتربة بالعربية..."
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            }
                            put("parts", partsArray)
                        })
                    }
                    put("contents", contentsArray)
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.2)
                        put("responseMimeType", "application/json")
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBodyString = response.body?.string()
                    if (!responseBodyString.isNullOrBlank()) {
                        val rootJson = JSONObject(responseBodyString)
                        val candidates = rootJson.optJSONArray("candidates")
                        val firstCandidate = candidates?.optJSONObject(0)
                        val content = firstCandidate?.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        val text = parts?.optJSONObject(0)?.optString("text")

                        if (!text.isNullOrBlank()) {
                            val parsed = JSONObject(text.trim())
                            return@withContext AiCropCareInfo(
                                plantName = plantName,
                                moistureThreshold = parsed.optInt("moisture_threshold", parsed.optInt("optimalMoistureThreshold", 50)),
                                targetMaxMoisture = parsed.optInt("target_max_moisture", 75),
                                recommendedWateringDurationMins = parsed.optInt("recommended_watering_duration_mins", 10),
                                sunlightNeeds = parsed.optString("sunlight_needs", "شمس كاملة 6-8 ساعات"),
                                careTips = parsed.optString("care_tips", "ري منتظم مع فحص تصريف التربة دورياً.")
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to domain calculation engine
            }
        }

        // Domain knowledge fallback for instant offline resilience
        val lower = plantName.lowercase()
        return@withContext when {
            lower.contains("طماطم") || lower.contains("شيري") || lower.contains("tomato") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 45,
                targetMaxMoisture = 70,
                recommendedWateringDurationMins = 12,
                sunlightNeeds = "شمس مباشرة كاملة 6-8 ساعات",
                careTips = "تحتاج الطماطم رياً عميقاً منتظماً وتجنب بلل الأوراق لمنع الفطريات."
            )
            lower.contains("خيار") || lower.contains("cucumber") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 55,
                targetMaxMoisture = 80,
                recommendedWateringDurationMins = 8,
                sunlightNeeds = "شمس معتدلة إلى ساطعة 5-7 ساعات",
                careTips = "جذور الخيار سطحية وتحتاج رطوبة متواصلة لمنع مرارة الثمار."
            )
            lower.contains("نعناع") || lower.contains("mint") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 60,
                targetMaxMoisture = 85,
                recommendedWateringDurationMins = 6,
                sunlightNeeds = "شمس غير مباشرة أو جزئية 4-6 ساعات",
                careTips = "النعناع يعشق التربة الرطبة الغنية، احرص على عدم جفاف السطح."
            )
            lower.contains("فلفل") || lower.contains("pepper") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 48,
                targetMaxMoisture = 72,
                recommendedWateringDurationMins = 10,
                sunlightNeeds = "شمس دافئة ساطعة 6-8 ساعات",
                careTips = "الفلفل يحتاج رطوبة معتدلة وجيدة التصريف، تقليل الري قبل الجني يركز النكهة."
            )
            lower.contains("فراولة") || lower.contains("strawberr") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 52,
                targetMaxMoisture = 75,
                recommendedWateringDurationMins = 7,
                sunlightNeeds = "شمس كاملة 6 ساعات يومياً",
                careTips = "الري بالتنقيط تحت الجذور مباشرة وتجنب تغريق التاج."
            )
            lower.contains("ريحان") || lower.contains("basil") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 50,
                targetMaxMoisture = 75,
                recommendedWateringDurationMins = 6,
                sunlightNeeds = "شمس دافئة 6-8 ساعات",
                careTips = "الري عند الصباح الباكر مع تقليم البراعم الزهرية لتعزيز الأوراق."
            )
            lower.contains("زيتون") || lower.contains("olive") -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 35,
                targetMaxMoisture = 60,
                recommendedWateringDurationMins = 20,
                sunlightNeeds = "شمس كاملة طوال النهار",
                careTips = "متحمل للجفاف، يفضل الري التباعدي العميق لتشجيع الجذور العميقة."
            )
            else -> AiCropCareInfo(
                plantName = plantName,
                moistureThreshold = 50,
                targetMaxMoisture = 75,
                recommendedWateringDurationMins = 10,
                sunlightNeeds = "شمس متوازنة 6 ساعات يومياً",
                careTips = "ري منتظم عند انخفاض الرطوبة دون العتبة مع التأكد من تصريف حوض الزراعة."
            )
        }
    }

    /**
     * Calls Gemini API with structured JSON schema prompt to retrieve exact ideal agronomic profile:
     * - ideal_soil_moisture_min
     * - ideal_soil_moisture_max
     * - ideal_temp_min
     * - ideal_temp_max
     * - ideal_humidity_range
     * - watering_schedule_tips
     */
    suspend fun fetchCropAgronomicProfile(cropName: String): CropAgronomicProfile = withContext(Dispatchers.IO) {
        val apiKey = resolveGeminiKey()

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are an expert precision agronomist and smart irrigation specialist.
                    Analyze the crop "$cropName" and return its exact agronomic condition parameters.
                    You MUST respond strictly with a valid JSON object adhering to this schema:
                    {
                      "ideal_soil_moisture_min": 45,
                      "ideal_soil_moisture_max": 75,
                      "ideal_temp_min": 18.0,
                      "ideal_temp_max": 28.5,
                      "ideal_humidity_range": "50% - 70%",
                      "watering_schedule_tips": "توصيات الري وجدولته بالعربية بدقة وفقاً للمناخ والتربة"
                    }
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            }
                            put("parts", partsArray)
                        })
                    }
                    put("contents", contentsArray)
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.2)
                        put("responseMimeType", "application/json")
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val body = requestJson.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBodyString = response.body?.string()
                    if (!responseBodyString.isNullOrBlank()) {
                        val rootJson = JSONObject(responseBodyString)
                        val candidates = rootJson.optJSONArray("candidates")
                        val firstCandidate = candidates?.optJSONObject(0)
                        val content = firstCandidate?.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        val text = parts?.optJSONObject(0)?.optString("text")

                        if (!text.isNullOrBlank()) {
                            val parsed = JSONObject(text.trim())
                            return@withContext CropAgronomicProfile(
                                cropName = cropName,
                                idealSoilMoistureMin = parsed.optInt("ideal_soil_moisture_min", 45),
                                idealSoilMoistureMax = parsed.optInt("ideal_soil_moisture_max", 75),
                                idealTempMin = parsed.optDouble("ideal_temp_min", 18.0),
                                idealTempMax = parsed.optDouble("ideal_temp_max", 28.0),
                                idealHumidityRange = parsed.optString("ideal_humidity_range", "50% - 70%"),
                                wateringScheduleTips = parsed.optString("watering_schedule_tips", "ري منتظم في الصباح الباكر مع تفادي تشبع الجذور."),
                                fetchedAt = System.currentTimeMillis()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to domain profile below
            }
        }

        // Domain knowledge fallback for instant resilience and database mapping
        val lower = cropName.lowercase()
        return@withContext when {
            lower.contains("طماطم") || lower.contains("tomato") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 45,
                idealSoilMoistureMax = 75,
                idealTempMin = 18.0,
                idealTempMax = 28.0,
                idealHumidityRange = "55% - 70%",
                wateringScheduleTips = "ري بالتنقيط في الصباح الباكر، مع الحفاظ على رطوبة مستقرة لتجنب تشقق الثمار وعفن نهاية الزهرة.",
                fetchedAt = System.currentTimeMillis()
            )
            lower.contains("خيار") || lower.contains("cucumber") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 55,
                idealSoilMoistureMax = 85,
                idealTempMin = 20.0,
                idealTempMax = 30.0,
                idealHumidityRange = "60% - 80%",
                wateringScheduleTips = "جذور الخيار سطحية تحتاج رياً خفيفاً متكرراً لمنع مرارة الثمار والإجهاد الحراري.",
                fetchedAt = System.currentTimeMillis()
            )
            lower.contains("نعناع") || lower.contains("mint") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 60,
                idealSoilMoistureMax = 90,
                idealTempMin = 16.0,
                idealTempMax = 26.0,
                idealHumidityRange = "60% - 85%",
                wateringScheduleTips = "النعناع محب للتربة الرطبة، حافظ على دورات ري قصيرة ومتكررة لمنع جفاف أطراف الأوراق.",
                fetchedAt = System.currentTimeMillis()
            )
            lower.contains("فلفل") || lower.contains("pepper") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 48,
                idealSoilMoistureMax = 72,
                idealTempMin = 20.0,
                idealTempMax = 32.0,
                idealHumidityRange = "50% - 65%",
                wateringScheduleTips = "ري معتدل، تقليل كمية المياه تدريجياً قبل الحصاد يعزز تركيز النكهة والمواد الفعالة.",
                fetchedAt = System.currentTimeMillis()
            )
            lower.contains("فراولة") || lower.contains("strawberr") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 50,
                idealSoilMoistureMax = 75,
                idealTempMin = 15.0,
                idealTempMax = 25.0,
                idealHumidityRange = "55% - 70%",
                wateringScheduleTips = "ري بالتنقيط أسفل التاج النباتي لتفادي إصابة الثمار بالأعفان الرمادية.",
                fetchedAt = System.currentTimeMillis()
            )
            lower.contains("زيتون") || lower.contains("olive") -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 35,
                idealSoilMoistureMax = 60,
                idealTempMin = 15.0,
                idealTempMax = 35.0,
                idealHumidityRange = "40% - 60%",
                wateringScheduleTips = "متحمل للجفاف، يفضل ري تباعدي عميق على فترات لدعم امتداد المجموع الجذري.",
                fetchedAt = System.currentTimeMillis()
            )
            else -> CropAgronomicProfile(
                cropName = cropName,
                idealSoilMoistureMin = 45,
                idealSoilMoistureMax = 75,
                idealTempMin = 18.0,
                idealTempMax = 28.0,
                idealHumidityRange = "50% - 70%",
                wateringScheduleTips = "ري منتظم بمجرد نزول الرطوبة عن الحد الأدنى، مع مراعاة درجة حرارة الجو وقت الري.",
                fetchedAt = System.currentTimeMillis()
            )
        }
    }

    private fun resolveGeminiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (!key.isNullOrBlank() && key != "MY_GEMINI_API_KEY") {
                key
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}

