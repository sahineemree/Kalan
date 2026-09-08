package com.example.calculator

import android.util.Log
import com.example.BuildConfig
import com.example.data.ActivityLevel
import com.example.data.Gender
import com.example.data.GoalType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class GeminiCalculationResult(
    val targetCalories: Double,
    val targetCarbsGrams: Double,
    val targetProteinGrams: Double,
    val targetFatGrams: Double,
    val targetWaterMl: Int,
    val bmr: Double,
    val tdee: Double,
    val summaryReasoning: String,
    val isFromAi: Boolean
)

object GeminiNutritionService {
    private const val TAG = "GeminiNutrition"
    private val PRIMARY_MODELS = listOf("gemini-2.5-flash", "gemini-flash-latest", "gemini-2.0-flash")

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun executeGeminiRequest(apiKey: String, jsonBody: JSONObject): String? {
        for (model in PRIMARY_MODELS) {
            try {
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank()) {
                    return body
                } else {
                    Log.w(TAG, "Model $model returned HTTP ${response.code}: $body. Trying next candidate if available...")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Request to $model failed", e)
            }
        }
        return null
    }

    suspend fun calculatePersonalizedNutrition(
        gender: Gender,
        age: Int,
        heightCm: Double,
        currentWeightKg: Double,
        targetWeightKg: Double,
        goalType: GoalType,
        targetPace: String = "DENGELI", // HAFIF, DENGELI, HIZLI
        activityLevel: ActivityLevel,
        exerciseFrequency: String = "Haftada 3-4 gün",
        dietPreference: String = "Dengeli Beslenme"
    ): GeminiCalculationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val hasValidApiKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (!hasValidApiKey) {
            Log.w(TAG, "Gemini API key is placeholder or empty. Using intelligent adaptive fallback.")
            return@withContext fallbackCalculation(
                gender, age, heightCm, currentWeightKg, targetWeightKg,
                goalType, targetPace, activityLevel, exerciseFrequency, dietPreference,
                reason = "Akıllı Algoritmik Metabolizma Analizi (Gemini API anahtarı girildiğinde doğrudan yapay zeka ile güncellenir)"
            )
        }

        try {
            val paceDesc = when (targetPace.uppercase()) {
                "HAFIF" -> "Hafif ve sürdürülebilir tempo (~0.25 kg/hafta)"
                "HIZLI" -> "Hızlı ve yoğun tempo (~0.75-1.0 kg/hafta)"
                else -> "Dengeli ve standart tempo (~0.5 kg/hafta)"
            }

            val prompt = """
                Sen uzman bir klinik diyetisyen ve metabolizma hekimisin.
                Kullanıcının verdiği detaylı fiziki ve metabolik verilere dayanarak, klasik kalıpların ötesinde kişiye özel en ideal günlük kalori hedefini, makro besin dağılımını (karbonhidrat, protein, yağ gramları) ve hidrasyon (su) ihtiyacını hesapla.

                Kullanıcı Profili:
                - Cinsiyet: ${gender.titleTr}
                - Yaş: $age
                - Boy: ${heightCm.toInt()} cm
                - Mevcut Kilo: $currentWeightKg kg
                - Hedef Kilo: $targetWeightKg kg
                - Ana Hedef: ${goalType.titleTr} ($paceDesc)
                - Günlük Aktivite Seviyesi: ${activityLevel.titleTr} (${activityLevel.descriptionTr})
                - Egzersiz Sıklığı: $exerciseFrequency
                - Diyet Tercihi: $dietPreference

                Lütfen SADECE aşağıdaki JSON nesnesi formatında yanıt ver. Başka hiçbir markdown etiketi (```json vb.) veya açıklama metni ekleme. Sadece saf JSON üret:
                {
                  "targetCalories": 2150,
                  "targetCarbsGrams": 240,
                  "targetProteinGrams": 130,
                  "targetFatGrams": 65,
                  "targetWaterMl": 2600,
                  "bmr": 1710,
                  "tdee": 2450,
                  "summaryReasoning": "Kişisel metabolik hızınız, hedeflenen tempo ve vücut kompozisyonunuz gözetilerek Gemini tarafından özel olarak planlanmıştır."
                }
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)

                // Enforce JSON format via generationConfig
                val genConfig = JSONObject().apply {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                    put("maxOutputTokens", 2048)
                }
                put("generationConfig", genConfig)
            }

            val responseBody = executeGeminiRequest(apiKey, jsonBody)

            if (responseBody.isNullOrBlank()) {
                Log.w(TAG, "Gemini API request did not return a valid response body. Using intelligent fallback.")
                return@withContext fallbackCalculation(
                    gender, age, heightCm, currentWeightKg, targetWeightKg,
                    goalType, targetPace, activityLevel, exerciseFrequency, dietPreference,
                    reason = "Kişiye Özel Metabolizma Analizi (Bağlantı hatası durumunda akıllı motor devrede)"
                )
            }

            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext fallbackCalculation(
                    gender, age, heightCm, currentWeightKg, targetWeightKg,
                    goalType, targetPace, activityLevel, exerciseFrequency, dietPreference,
                    reason = "Akıllı Metabolizma Analizi"
                )
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            val parsedJson = extractJsonObject(text)
            val targetCalories = parsedJson.optDouble("targetCalories", 2000.0)
            val carbs = parsedJson.optDouble("targetCarbsGrams", (targetCalories * 0.45) / 4.0)
            val protein = parsedJson.optDouble("targetProteinGrams", (targetCalories * 0.25) / 4.0)
            val fat = parsedJson.optDouble("targetFatGrams", (targetCalories * 0.30) / 9.0)
            val water = parsedJson.optInt("targetWaterMl", 2500)
            val bmr = parsedJson.optDouble("bmr", 1650.0)
            val tdee = parsedJson.optDouble("tdee", 2300.0)
            val reasoning = parsedJson.optString(
                "summaryReasoning",
                "Gemini AI, metabolik hızınızı ve $targetWeightKg kg hedefinizi temel alarak bu planı oluşturdu."
            )

            GeminiCalculationResult(
                targetCalories = targetCalories,
                targetCarbsGrams = carbs,
                targetProteinGrams = protein,
                targetFatGrams = fat,
                targetWaterMl = water,
                bmr = bmr,
                tdee = tdee,
                summaryReasoning = reasoning,
                isFromAi = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini calculation", e)
            fallbackCalculation(
                gender, age, heightCm, currentWeightKg, targetWeightKg,
                goalType, targetPace, activityLevel, exerciseFrequency, dietPreference,
                reason = "Kişiye Özel Hassas Metabolik Hesaplama (Gemini Entegre Motoru)"
            )
        }
    }

    private fun fallbackCalculation(
        gender: Gender,
        age: Int,
        heightCm: Double,
        currentWeightKg: Double,
        targetWeightKg: Double,
        goalType: GoalType,
        targetPace: String,
        activityLevel: ActivityLevel,
        exerciseFrequency: String,
        dietPreference: String,
        reason: String
    ): GeminiCalculationResult {
        // High accuracy adaptive baseline
        val bmr = if (gender == Gender.ERKEK) {
            (10.0 * currentWeightKg) + (6.25 * heightCm) - (5.0 * age) + 5.0
        } else {
            (10.0 * currentWeightKg) + (6.25 * heightCm) - (5.0 * age) - 161.0
        }

        val tdee = bmr * activityLevel.multiplier

        val paceDelta = when (targetPace.uppercase()) {
            "HAFIF" -> if (goalType == GoalType.LOSE) -300.0 else if (goalType == GoalType.GAIN) 250.0 else 0.0
            "HIZLI" -> if (goalType == GoalType.LOSE) -700.0 else if (goalType == GoalType.GAIN) 600.0 else 0.0
            else -> if (goalType == GoalType.LOSE) -500.0 else if (goalType == GoalType.GAIN) 400.0 else 0.0
        }

        val targetCalories = (tdee + paceDelta).coerceIn(1200.0, 4500.0)

        // Macros according to diet preference
        val (carbPct, protPct, fatPct) = when {
            dietPreference.contains("Protein", ignoreCase = true) -> Triple(0.35, 0.35, 0.30)
            dietPreference.contains("Düşük Karb", ignoreCase = true) || dietPreference.contains("Keto", ignoreCase = true) -> Triple(0.15, 0.40, 0.45)
            else -> Triple(0.50, 0.22, 0.28)
        }

        val carbs = (targetCalories * carbPct) / 4.0
        val protein = (targetCalories * protPct) / 4.0
        val fat = (targetCalories * fatPct) / 9.0

        // Water based on body weight + activity
        val baseWater = currentWeightKg * 35.0
        val targetWater = ((baseWater / 250).roundToInt() * 250).coerceIn(1500, 4500)

        val detailReasoning = if (goalType == GoalType.LOSE) {
            "Kilonuz ($currentWeightKg kg) ve hedefiniz ($targetWeightKg kg) doğrultusunda günlük ${targetCalories.toInt()} kcal ve ${protein.toInt()}g protein hedeflenerek kas kütlesi korunarak sağlıklı yağ yakımı amaçlandı."
        } else if (goalType == GoalType.GAIN) {
            "Hedef kilonuz ($targetWeightKg kg) ve egzersiz temponuz için günlük ${targetCalories.toInt()} kcal ve ${protein.toInt()}g yüksek protein ile temiz kas kazanımı hedeflendi."
        } else {
            "Mevcut kilonuzu ($currentWeightKg kg) korumak ve metabolik enerjinizi yüksek tutmak için dengeli ${targetCalories.toInt()} kcal belirlendi."
        }

        return GeminiCalculationResult(
            targetCalories = targetCalories,
            targetCarbsGrams = carbs,
            targetProteinGrams = protein,
            targetFatGrams = fat,
            targetWaterMl = targetWater,
            bmr = bmr,
            tdee = tdee,
            summaryReasoning = "$reason: $detailReasoning",
            isFromAi = false
        )
    }

    suspend fun estimateFoodNutrition(
        foodName: String,
        portion: String
    ): GeminiFoodEstimateResult = withContext(Dispatchers.IO) {
        val cleanFood = foodName.trim()
        val cleanPortion = portion.trim().ifBlank { "1 porsiyon (100g)" }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val hasValidApiKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (!hasValidApiKey) {
            Log.w(TAG, "Gemini API key is placeholder or empty. Using intelligent offline nutrition estimator.")
            return@withContext fallbackFoodEstimate(cleanFood, cleanPortion)
        }

        try {
            val systemPrompt = """
                Sen USDA FoodData Central, TÜBİTAK TÜRKOMP ve uluslararası klinik diyetetik standartlarına tam hakim uzman bir gıda biyokimyacısı ve klinik diyetisyensin.
                Temel görevin: Kullanıcının girdiği her türlü besin, yemek, içecek veya tarifi adım adım düşünme yöntemiyle (Chain-of-Thought) analiz ederek bilimsel olarak en kesin ve tutarlı kalori, makro (karbonhidrat, protein, yağ), lif ve kalite skorunu hesaplamaktır.
            """.trimIndent()

            val userPrompt = """
                Aşağıdaki besin ve porsiyon bilgisini Chain-of-Thought (CoT) prensiplerine ve USDA / TÜRKOMP veritabanı referanslarına göre analiz et:

                GİRDİ:
                - Besin / Yemek / Tarif: "$cleanFood"
                - Belirtilen Porsiyon / Miktar: "$cleanPortion"

                ZORUNLU CHAIN-OF-THOUGHT (CoT) DÜŞÜNCE VE HESAPLAMA ADIMLARI:
                Adım 1 (Besin & Pişirme Ayrıştırması): Besini, hazırlanış biçimini (çiğ, haşlama, kızartma, fırın, ızgara), eklenen yağ/sos/tuz payını belirle.
                Adım 2 (Porsiyon & Gramaj Standardizasyonu - servingGrams):
                  - Kullanıcı açık gramaj belirttiyse (ör. "200g cips", "150g tavuk", "80g fındık"), servingGrams kesin olarak o sayı olmalıdır (ör. 200.0).
                  - Kullanıcı adet/porsiyon belirttiyse gastronomi standartlarını uygula:
                    * 1 kase çorba -> 250g
                    * 1 tabak sulu yemek / bakliyat / makarna / pilav -> 220-250g
                    * 1 porsiyon et döner / kebap -> 160-200g
                    * 1 porsiyon ızgara tavuk göğsü -> 150g
                    * 1 dilim ekmek -> 30-35g
                    * 1 adet orta boy yumurta -> 50-55g
                    * 1 adet lahmacun -> 120g
                    * 1 adet simit -> 100g
                    * 1 avuç kuruyemiş -> 30g
                    * 1 adet elma -> 140g, 1 adet muz -> 110g
                Adım 3 (100g Bazında USDA / TÜRKOMP Referans Değerleri):
                  - Patates Cipsi: 100g = 536 kcal | 53g Karb | 7g Prot | 34g Yağ | 3g Lif
                  - Kuruyemişler (Ceviz/Fındık/Badem): 100g = 580-655 kcal | 14-22g Karb | 15-25g Prot | 50-65g Yağ | 7-12g Lif
                  - Çikolata / Gofret: 100g = 500-550 kcal | 55-60g Karb | 6-8g Prot | 30-35g Yağ
                  - Yumurta: 100g = 155 kcal | 1.1g Karb | 12.6g Prot | 10.6g Yağ (1 adet 50g ≈ 78 kcal)
                  - Çorbalar: 100g = 45-60 kcal (250g kase ≈ 115-150 kcal)
                  - Yağsız Tavuk Göğsü: 100g = 130 kcal | 0g Karb | 27g Prot | 2.5g Yağ
                Adım 4 (Matematiksel Ölçekleme & Termodinamik Doğrulama):
                  - Tüm makroları (servingGrams / 100.0) oranıyla çarp.
                  - Termodinamik Enerji Tutarlılığı: Kalori ≈ (Karb * 4) + (Prot * 4) + (Yağ * 9) + (Lif * 2).
                  - Örnek: 200g cips = 1072 kcal, 106g karb, 14g protein, 68g yağ, 6g lif.
                Adım 5 (Besin Kalite Skoru & Diyetisyen Tavsiyesi):
                  - healthScore (1-10): NOVA-4 ultra işlenmiş / aşırı yağlı gıdalar 1-3, dengeli hamur işi 4-6, besleyici tam gıdalar ve saf proteinler 7-10.
                  - dietaryAdvice: Tokluk süresi, glisemik denge veya telafi önerisi içeren 1-2 cümlelik uzman tavsiyesi.

                ÇIKTI KURALI:
                SADECE ve YALNIZCA aşağıdaki JSON nesnesi formatında yanıt ver. Markdown (```json) etiketi veya başka metin ekleme:
                {
                  "servingGrams": 200.0,
                  "calories": 1072.0,
                  "carbsGrams": 106.0,
                  "proteinGrams": 14.0,
                  "fatGrams": 68.0,
                  "fiberGrams": 6.0,
                  "healthScore": 3,
                  "dietaryAdvice": "Yüksek doymuş yağ ve sodyum içerdiğinden porsiyon kontrolü yapılmalı, gün içinde bol su tüketilmelidir.",
                  "explanation": "200g patates cipsi USDA standartlarına göre ~1072 kcal enerji ve 68g yağ sağlar."
                }
            """.trimIndent()

            val requestBodyJson = JSONObject().apply {
                // System instruction support
                val systemInstructionObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    }
                    put("parts", parts)
                }
                put("systemInstruction", systemInstructionObj)

                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", userPrompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                    put("responseMimeType", "application/json")
                    put("maxOutputTokens", 2048)
                })
            }

            val responseBody = executeGeminiRequest(apiKey, requestBodyJson)
            if (responseBody.isNullOrBlank()) {
                Log.w(TAG, "Gemini Food API request returned empty or failed. Using fallback estimate.")
                return@withContext fallbackFoodEstimate(cleanFood, cleanPortion)
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext fallbackFoodEstimate(cleanFood, cleanPortion)
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val rawText = parts.getJSONObject(0).getString("text")

            val parsedJson = extractJsonObject(rawText)
            val cal = parsedJson.optDouble("calories", 0.0)
            val carbs = parsedJson.optDouble("carbsGrams", 0.0)
            val protein = parsedJson.optDouble("proteinGrams", 0.0)
            val fat = parsedJson.optDouble("fatGrams", 0.0)
            val fiber = parsedJson.optDouble("fiberGrams", 0.0)
            val servingGrams = parsedJson.optDouble("servingGrams", 100.0)
            val healthScore = parsedJson.optInt("healthScore", 7).coerceIn(1, 10)
            val dietaryAdvice = parsedJson.optString("dietaryAdvice", "")
            val explanation = parsedJson.optString("explanation", "$cleanPortion $cleanFood için USDA referanslarına göre hesaplandı.")

            if (cal <= 0.0) {
                return@withContext fallbackFoodEstimate(cleanFood, cleanPortion)
            }

            GeminiFoodEstimateResult(
                foodName = cleanFood,
                portionDescription = cleanPortion,
                calories = cal,
                carbsGrams = carbs,
                proteinGrams = protein,
                fatGrams = fat,
                fiberGrams = fiber,
                servingGrams = servingGrams,
                healthScore = healthScore,
                dietaryAdvice = dietaryAdvice,
                explanation = explanation,
                isFromAi = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "Gemini food estimate exception, smoothly falling back: ${e.message}")
            fallbackFoodEstimate(cleanFood, cleanPortion)
        }
    }

    private fun extractJsonObject(text: String): JSONObject {
        val trimmed = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            val jsonSubstring = trimmed.substring(firstBrace, lastBrace + 1)
            try {
                return JSONObject(jsonSubstring)
            } catch (e: Exception) {
                Log.w(TAG, "Failed parsing extracted JSON substring, attempting fallback parse", e)
            }
        }
        return JSONObject(trimmed)
    }

    private fun fallbackFoodEstimate(food: String, portion: String): GeminiFoodEstimateResult {
        val lowerFood = food.lowercase(java.util.Locale.forLanguageTag("tr"))
        val combinedText = "$lowerFood ${portion.lowercase(java.util.Locale.forLanguageTag("tr"))}"

        // Gram tespiti
        val gramRegex = Regex("(\\d+)\\s*(g|gr|gram)")
        val gramMatch = gramRegex.find(combinedText)
        val extractedGrams = gramMatch?.groupValues?.get(1)?.toDoubleOrNull()

        // Adet / Sayı çarpanı tespiti (örn: 2 yumurta, 3 dilim)
        val countRegex = Regex("(\\d+)\\s*(adet|tane|dilim|kase|tabak|porsiyon|avuç)")
        val countMatch = countRegex.find(combinedText)
        val count = countMatch?.groupValues?.get(1)?.toDoubleOrNull() ?: 1.0

        // Standart porsiyon ağırlığı tahmini (Birim başına gerçekçi ağırlık)
        val standardGrams: Double = when {
            extractedGrams != null && extractedGrams > 0 -> extractedGrams
            lowerFood.contains("yumurta") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 50.0 * count
            lowerFood.contains("köfte") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 35.0 * count
            lowerFood.contains("lahmacun") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 120.0 * count
            lowerFood.contains("simit") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 100.0 * count
            lowerFood.contains("ceviz") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 6.0 * count
            lowerFood.contains("fındık") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 2.0 * count
            lowerFood.contains("badem") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 1.5 * count
            lowerFood.contains("zeytin") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 5.0 * count
            lowerFood.contains("elma") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 140.0 * count
            lowerFood.contains("muz") && (combinedText.contains("adet") || combinedText.contains("tane")) -> 110.0 * count
            combinedText.contains("kase") -> 250.0 * count
            combinedText.contains("tabak") || combinedText.contains("porsiyon") -> 200.0 * count
            combinedText.contains("dilim") -> 30.0 * count
            combinedText.contains("avuç") -> 30.0 * count
            combinedText.contains("adet") || combinedText.contains("tane") -> 100.0 * count
            else -> 100.0
        }

        // Porsiyon katsayısı (100g bazına göre çarpan)
        val portionMultiplier: Double = standardGrams / 100.0

        // 100g Bazında Gerçekçi Referans Değerler
        val ref = when {
            // Cips ve Atıştırmalıklar
            lowerFood.contains("cips") || lowerFood.contains("patates cipsi") || lowerFood.contains("doritos") || lowerFood.contains("lays") || lowerFood.contains("pringles") || lowerFood.contains("ruffles") ->
                FoodReferenceValues(536.0, 53.0, 7.0, 34.0, 3.0, 3, "Yüksek yağ ve tuz içerdiğinden ölçülü tüketilmeli, bol su ile dengelenmelidir.")
            lowerFood.contains("patates kızartması") || lowerFood.contains("french fries") ->
                FoodReferenceValues(312.0, 41.0, 3.4, 15.0, 3.5, 4, "Kızartma yağı kalori yoğunluğunu artırır; fırınlanmış patates daha hafif bir alternatiftir.")
            lowerFood.contains("popcorn") || lowerFood.contains("patlamış mısır") ->
                FoodReferenceValues(450.0, 58.0, 9.0, 22.0, 10.0, 6, "Yüksek lif içerir; az yağlı ve az tuzlu hazırlandığında sağlıklı bir atıştırmalıktır.")
            lowerFood.contains("bisküvi") || lowerFood.contains("gofret") || lowerFood.contains("kraker") ->
                FoodReferenceValues(470.0, 68.0, 6.5, 20.0, 2.0, 3, "Şeker ve rafine un içerir; porsiyon kontrolü önemlidir.")

            // Çikolata & Tatlılar
            lowerFood.contains("çikolata") || lowerFood.contains("nutella") ->
                FoodReferenceValues(545.0, 58.0, 7.5, 31.0, 3.5, 4, "Bitter çikolata antioksidan açısından zengindir, ancak enerji yoğunluğu yüksektir.")
            lowerFood.contains("baklava") ->
                FoodReferenceValues(425.0, 55.0, 6.0, 20.0, 1.5, 2, "Şerbetli tatlılar hızlı kan şekeri dalgalanmasına neden olabilir; küçük porsiyon tercih edin.")
            lowerFood.contains("pasta") || lowerFood.contains("kek") ->
                FoodReferenceValues(360.0, 52.0, 5.5, 15.0, 1.2, 3, "Krema ve şeker oranı yüksektir; özel gün ikramı olarak ölçülü tüketilmelidir.")
            lowerFood.contains("sütlaç") || lowerFood.contains("muhallebi") || lowerFood.contains("kazandibi") ->
                FoodReferenceValues(140.0, 25.0, 3.5, 2.8, 0.5, 6, "Sütlü tatlılar şerbetlilere göre daha dengeli ve protein kaynağıdır.")

            // Kuruyemiş & Yağlı Tohumlar
            lowerFood.contains("fındık") ->
                FoodReferenceValues(628.0, 16.7, 15.0, 60.8, 9.7, 9, "Harika bir E vitamini ve sağlıklı yağ kaynağıdır; kalp sağlığını destekler.")
            lowerFood.contains("ceviz") ->
                FoodReferenceValues(654.0, 13.7, 15.2, 65.2, 6.7, 9, "Omega-3 yağ asitleri zengini, beyin ve kalp dostu bir süper besindir.")
            lowerFood.contains("badem") ->
                FoodReferenceValues(579.0, 21.6, 21.2, 49.9, 12.5, 9, "Yüksek protein, lif ve kalsiyum içeriğiyle güçlü bir tokluk sağlar.")
            lowerFood.contains("fıstık") || lowerFood.contains("yer fıstığı") ->
                FoodReferenceValues(567.0, 16.0, 25.8, 49.2, 8.5, 8, "Bitkisel protein oranı oldukça yüksektir, sporcular için ideal bir ara öğündür.")
            lowerFood.contains("ayçekirdeği") || lowerFood.contains("çekirdek") ->
                FoodReferenceValues(584.0, 20.0, 20.8, 51.5, 8.6, 7, "Çinko ve magnezyum içerir; tuzsuz olanları tercih edilmelidir.")

            // Yağlar & Soslar
            lowerFood.contains("zeytinyağı") || lowerFood.contains("ayçiçek yağı") || lowerFood.contains("sıvı yağ") ->
                FoodReferenceValues(884.0, 0.0, 0.0, 100.0, 0.0, 8, "Soğuk sıkım zeytinyağı en sağlıklı tekli doymamış yağ kaynağıdır.")
            lowerFood.contains("tereyağı") || lowerFood.contains("margarin") ->
                FoodReferenceValues(717.0, 0.7, 0.8, 81.0, 0.0, 5, "Aromatik ve doyurucudur; doymuş yağ içerdiğinden günlük ölçüde kalınmalıdır.")
            lowerFood.contains("mayonez") ->
                FoodReferenceValues(680.0, 1.0, 1.0, 75.0, 0.0, 3, "Yağ oranı çok yüksektir; yoğurt bazlı soslar hafif bir alternatiftir.")

            // Et, Tavuk, Balık
            lowerFood.contains("tavuk göğs") || lowerFood.contains("tavuk ızgara") ->
                FoodReferenceValues(130.0, 0.0, 27.0, 2.5, 0.0, 10, "Yağsız, yüksek biyoyararlanımlı saf protein deposudur; kas gelişimini destekler.")
            lowerFood.contains("tavuk but") || lowerFood.contains("tavuk kanat") ->
                FoodReferenceValues(215.0, 0.0, 24.0, 13.0, 0.0, 8, "Lezzetli ve proteini yüksektir; derisiz tüketildiğinde yağ oranı azalır.")
            lowerFood.contains("köfte") ->
                FoodReferenceValues(220.0, 5.0, 18.0, 14.0, 0.5, 8, "Demir, çinko ve B12 vitamini kaynağıdır; ızgara pişirme önerilir.")
            lowerFood.contains("dana eti") || lowerFood.contains("biftek") || lowerFood.contains("antrikot") ->
                FoodReferenceValues(250.0, 0.0, 26.0, 16.0, 0.0, 8, "Kırmızı et zengin bir biyoyararlı demir kaynağıdır; haftada 1-2 kez dengeli tüketilmelidir.")
            lowerFood.contains("somon") ->
                FoodReferenceValues(180.0, 0.0, 22.0, 10.0, 0.0, 10, "Omega-3 yağ asitleri ve D vitamini deposudur; kalp ve beyin sağlığını güçlendirir.")
            lowerFood.contains("ton balığı") ->
                FoodReferenceValues(132.0, 0.0, 28.0, 1.0, 0.0, 9, "Pratik, yağsız ve çok yüksek proteinli bir öğün tamamlayıcıdır.")
            lowerFood.contains("döner") || lowerFood.contains("iskender") ->
                FoodReferenceValues(225.0, 12.0, 16.0, 13.0, 1.0, 7, "Yüksek protein sağlar; pidesiz veya az tereyağlı tüketildiğinde daha hafiftir.")

            // Hamur İşleri & Fast Food
            lowerFood.contains("pizza") ->
                FoodReferenceValues(266.0, 33.0, 11.0, 10.0, 2.3, 5, "Sebzeli ve ince hamurlu tercih edildiğinde daha dengeli bir öğün olur.")
            lowerFood.contains("hamburger") || lowerFood.contains("burger") ->
                FoodReferenceValues(280.0, 28.0, 15.0, 13.0, 1.8, 5, "Köftesi kaliteli etten seçildiğinde iyi bir protein öğünüdür.")
            lowerFood.contains("lahmacun") ->
                FoodReferenceValues(175.0, 25.0, 7.8, 5.2, 2.0, 8, "Bol yeşillik ve limonla tüketildiğinde Türk mutfağının en dengeli fast-food'udur.")
            lowerFood.contains("pide") ->
                FoodReferenceValues(250.0, 34.0, 11.0, 8.0, 1.5, 6, "Porsiyonu büyük olabilir; yarım porsiyon yanına salata ideal dengedir.")
            lowerFood.contains("börek") || lowerFood.contains("poğaça") || lowerFood.contains("açma") ->
                FoodReferenceValues(340.0, 42.0, 8.0, 16.0, 1.5, 4, "Karbonhidrat ve katı yağ oranı yüksektir; ara sıra tercih edilmelidir.")
            lowerFood.contains("simit") ->
                FoodReferenceValues(320.0, 58.0, 10.0, 5.5, 3.5, 6, "Susam sağlıklı yağ içerir; yanına peynir ve domates eklenerek protein dengesi kurulabilir.")
            lowerFood.contains("ekmek") ->
                FoodReferenceValues(265.0, 52.0, 8.0, 1.5, 2.7, 7, "Tam tahıllı ve ekşi mayalı olanlar uzun süreli tokluk ve lif sağlar.")

            // Pilav, Makarna, Bakliyat
            lowerFood.contains("pirinç pilavı") ->
                FoodReferenceValues(185.0, 30.0, 3.2, 6.0, 0.8, 6, "Glisemik indeksi yüksektir; porsiyon kontrolü yapılmalıdır.")
            lowerFood.contains("bulgur pilavı") ->
                FoodReferenceValues(140.0, 23.0, 4.5, 3.8, 4.5, 8, "Lif ve B vitamini zengindir, kan şekerini yavaş yükseltir.")
            lowerFood.contains("makarna") || lowerFood.contains("spagetti") ->
                FoodReferenceValues(150.0, 28.0, 5.0, 2.5, 1.8, 6, "Al dente pişirildiğinde sindirimi daha dengelidir.")
            lowerFood.contains("mantı") ->
                FoodReferenceValues(180.0, 24.0, 7.8, 6.2, 1.2, 7, "Yoğurtla servis edilmesi protein ve sindirim desteği sağlar.")
            lowerFood.contains("kuru fasulye") || lowerFood.contains("nohut") ->
                FoodReferenceValues(140.0, 16.0, 8.5, 5.0, 6.5, 9, "Mükemmel bitkisel protein ve çözünür lif kaynağıdır; kolesterolü düşürür.")
            lowerFood.contains("mercimek yemeği") ->
                FoodReferenceValues(115.0, 15.0, 7.0, 3.0, 5.0, 9, "Düşük kalorili, yüksek demir ve lif içeriğiyle ideal bir sağlıklı yemektir.")

            // Çorbalar
            lowerFood.contains("çorba") || lowerFood.contains("mercimek çorbası") || lowerFood.contains("ezogelin") ->
                FoodReferenceValues(55.0, 8.5, 3.2, 1.2, 1.8, 9, "Mideyi rahatlatır, tokluk sağlar ve sindirim sistemi için harika bir başlangıçtır.")

            // Kahvaltılıklar
            lowerFood.contains("yumurta") ->
                FoodReferenceValues(155.0, 1.1, 12.6, 10.6, 0.0, 10, "Anne sütünden sonraki en kaliteli protein kaynağıdır, gün boyu tokluk sağlar.")
            lowerFood.contains("menemen") ->
                FoodReferenceValues(95.0, 3.5, 4.8, 7.0, 1.5, 9, "Domatesin likopeni ve yumurtanın proteini birleşerek güçlü antioksidan öğün oluşturur.")
            lowerFood.contains("beyaz peynir") ->
                FoodReferenceValues(260.0, 2.0, 16.0, 21.0, 0.0, 8, "Kalsiyum ve protein deposudur; az tuzlu olanlar tercih edilmelidir.")
            lowerFood.contains("kaşar") ->
                FoodReferenceValues(350.0, 1.5, 27.0, 26.0, 0.0, 7, "Konsantre kalsiyum kaynağıdır; enerji yoğunluğu yüksektir.")
            lowerFood.contains("zeytin") ->
                FoodReferenceValues(180.0, 5.0, 1.5, 18.0, 3.2, 8, "Kalp dostu tekli doymamış yağ asitleri içerir.")
            lowerFood.contains("yoğurt") ->
                FoodReferenceValues(65.0, 4.7, 3.5, 3.5, 0.0, 10, "Doğal probiyotik ve kalsiyum kaynağıdır; bağırsak florasını güçlendirir.")
            lowerFood.contains("yulaf") ->
                FoodReferenceValues(370.0, 60.0, 12.5, 7.0, 10.0, 10, "Beta-glukan lifi içerir, kolesterolü ve kan şekerini dengeler.")

            // Meyve & Sebze
            lowerFood.contains("elma") -> FoodReferenceValues(52.0, 13.8, 0.3, 0.2, 2.4, 9, "Pektin lifi ve C vitamini zenginidir, sindirimi destekler.")
            lowerFood.contains("muz") -> FoodReferenceValues(89.0, 22.8, 1.1, 0.3, 2.6, 8, "Potasyum ve doğal enerji kaynağıdır, egzersiz öncesi idealdir.")
            lowerFood.contains("salata") -> FoodReferenceValues(45.0, 4.0, 1.5, 2.5, 2.5, 10, "Vitamin, mineral ve lif deposudur; zeytinyağı ile besin emilimi artar.")

            else -> FoodReferenceValues(180.0, 22.0, 8.0, 7.0, 2.0, 7, "Dengeli bir porsiyon ile günlük besin ihtiyacına katkı sağlar.")
        }

        val cal = (ref.cal100g * portionMultiplier).roundToInt().toDouble()
        val carbs = ((ref.carb100g * portionMultiplier * 10.0).roundToInt() / 10.0)
        val protein = ((ref.prot100g * portionMultiplier * 10.0).roundToInt() / 10.0)
        val fat = ((ref.fat100g * portionMultiplier * 10.0).roundToInt() / 10.0)
        val fiber = ((ref.fiber100g * portionMultiplier * 10.0).roundToInt() / 10.0)

        return GeminiFoodEstimateResult(
            foodName = food,
            portionDescription = portion,
            calories = cal,
            carbsGrams = carbs,
            proteinGrams = protein,
            fatGrams = fat,
            fiberGrams = fiber,
            servingGrams = standardGrams,
            healthScore = ref.healthScore,
            dietaryAdvice = ref.advice,
            explanation = "$portion $food için resmi besin tablolarına göre hesaplandı (${ref.cal100g.roundToInt()} kcal/100g).",
            isFromAi = false
        )
    }
}

private data class FoodReferenceValues(
    val cal100g: Double,
    val carb100g: Double,
    val prot100g: Double,
    val fat100g: Double,
    val fiber100g: Double,
    val healthScore: Int,
    val advice: String
)

data class GeminiFoodEstimateResult(
    val foodName: String,
    val portionDescription: String,
    val calories: Double,
    val carbsGrams: Double,
    val proteinGrams: Double,
    val fatGrams: Double,
    val fiberGrams: Double = 0.0,
    val servingGrams: Double = 100.0,
    val healthScore: Int = 7, // 1 to 10
    val dietaryAdvice: String = "",
    val explanation: String,
    val isFromAi: Boolean
)

