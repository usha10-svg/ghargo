package com.example.ai

import com.example.BuildConfig
import com.example.data.BookingEntity
import com.example.data.WorkerEntity
import com.example.viewmodel.RateCardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

enum class GharSathiActionType {
    RECOMMENDED_WORKERS,
    BOOKING_STATUS,
    RATE_CARD,
    EMERGENCY_DISPATCH,
    COOPERATIVE_TRANSPARENCY,
    GENERAL_CHAT
}

data class GharSathiMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "hi", // "hi" or "en"
    val actionType: GharSathiActionType = GharSathiActionType.GENERAL_CHAT,
    val recommendedWorkers: List<WorkerEntity> = emptyList(),
    val relatedBookings: List<BookingEntity> = emptyList(),
    val targetTrade: String? = null
)

class GharSathiService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Generates a response from gharSathi AI.
     * Tries Gemini API (gemini-3.5-flash) if key is present and valid;
     * seamlessly falls back to deep domain knowledge engine for instantaneous, reliable answers.
     */
    suspend fun getResponse(
        userInput: String,
        isHindi: Boolean,
        allWorkers: List<WorkerEntity>,
        allBookings: List<BookingEntity>,
        rateCards: List<RateCardItem>,
        currentUserPhone: String
    ): GharSathiMessage = withContext(Dispatchers.IO) {
        val queryLower = userInput.trim().lowercase()

        // 1. Detect Intent & Relevant Local Data
        val detectedTrade = detectTrade(queryLower)
        val matchingWorkers = if (detectedTrade != null) {
            allWorkers.filter { it.trade.equals(detectedTrade, ignoreCase = true) || it.specializations.contains(detectedTrade, ignoreCase = true) }
        } else {
            findMatchingWorkersByKeywords(queryLower, allWorkers)
        }

        val userBookings = allBookings.filter {
            it.customerPhone.replace(" ", "").contains(currentUserPhone.replace(" ", "").takeLast(8)) ||
            it.status in listOf("CONFIRMED", "WORKER_ASSIGNED", "WORKER_ARRIVING", "SERVICE_STARTED")
        }.take(3)

        val isBookingQuery = isBookingStatusQuery(queryLower)
        val isPaymentQuery = isPaymentOrRateQuery(queryLower)
        val isCoopQuery = isCooperativeQuery(queryLower)
        val isEmergencyQuery = isEmergencyQuery(queryLower)

        // 2. Try Gemini API first if configured
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        var geminiResponseText: String? = null
        if (hasValidKey) {
            try {
                geminiResponseText = callGeminiApi(
                    userInput = userInput,
                    isHindi = isHindi,
                    apiKey = apiKey,
                    allWorkers = allWorkers,
                    userBookings = userBookings,
                    detectedTrade = detectedTrade
                )
            } catch (_: Exception) {
                // Ignore network/API failure and use smart domain fallback
            }
        }

        // 3. Construct Final AI Message with Contextual Action Attachments
        if (isEmergencyQuery) {
            val text = geminiResponseText ?: if (isHindi) {
                "🚨 **24/7 आपातकालीन सहायता (Emergency SOS)**\n\nयदि आपके घर में शॉर्ट सर्किट, स्पार्किंग, वॉटर पाइप बर्स्ट या कोई तत्काल संकट है, तो हमारा सहकारी रैपिड रिस्पांस 15-30 मिनट में पहुँचता है। कोई अतिरिक्त सर्ज चार्ज नहीं लिया जाता।"
            } else {
                "🚨 **24/7 Emergency Dispatch (SOS)**\n\nFor urgent home emergencies like short circuits, sparking, or pipe bursts, our verified cooperative technicians are dispatched in 15-30 minutes with zero surge pricing."
            }
            return@withContext GharSathiMessage(
                text = text,
                isUser = false,
                language = if (isHindi) "hi" else "en",
                actionType = GharSathiActionType.EMERGENCY_DISPATCH,
                targetTrade = detectedTrade
            )
        }

        if (isBookingQuery) {
            val text = geminiResponseText ?: if (userBookings.isNotEmpty()) {
                val b = userBookings.first()
                if (isHindi) {
                    "📋 **आपकी सक्रिय बुकिंग का विवरण:**\n\n• सेवा: **${b.serviceTitle}**\n• कारीगर: **${b.workerName}** (${b.trade})\n• समय: **${b.scheduledDate} • ${b.scheduledTime}**\n• स्थिति: **${translateStatusToHindi(b.status)}**\n• कार्य शुरू करने का गुप्त OTP: **${b.otpCode}** (कारीगर के आने पर ही साझा करें)\n• देय राशि: ₹${b.totalAmount}"
                } else {
                    "📋 **Your Active Booking Details:**\n\n• Service: **${b.serviceTitle}**\n• Technician: **${b.workerName}** (${b.trade})\n• Schedule: **${b.scheduledDate} at ${b.scheduledTime}**\n• Live Status: **${b.status}**\n• Job Start OTP: **${b.otpCode}** (share only after technician arrives)\n• Total Amount: ₹${b.totalAmount}"
                }
            } else {
                if (isHindi) {
                    "📋 वर्तमान में आपकी कोई लंबित बुकिंग नहीं है। क्या आप किसी सत्यापित इलेक्ट्रीशियन, प्लम्बर या कारपेंटर को बुक करना चाहते हैं?"
                } else {
                    "📋 You have no active bookings right now. Would you like to find and book a verified electrician, plumber, or appliance technician?"
                }
            }
            return@withContext GharSathiMessage(
                text = text,
                isUser = false,
                language = if (isHindi) "hi" else "en",
                actionType = GharSathiActionType.BOOKING_STATUS,
                relatedBookings = userBookings
            )
        }

        if (matchingWorkers.isNotEmpty() && (detectedTrade != null || isWorkerSearchQuery(queryLower))) {
            val primaryWorker = matchingWorkers.first()
            val text = geminiResponseText ?: if (isHindi) {
                "🛠️ **सत्यापित सहकारी कारीगर मिल गए हैं!**\n\nमैंने आपके लिए **${detectedTrade ?: primaryWorker.trade}** के शीर्ष रेटेड कारीगर खोजे हैं:\n• **${primaryWorker.name}** (${primaryWorker.rating}★, ${primaryWorker.experienceYears} वर्ष अनुभव)\n• दर: ₹${primaryWorker.hourlyRate}/घंटा\n• सत्यापन: NSDC प्रमाणित एवं e-Shram पंजीकृत\n\nआप सीधे नीचे दिए गए कार्ड से प्रोफाइल देख सकते हैं या 1-क्लिक में बुक कर सकते हैं।"
            } else {
                "🛠️ **Verified Cooperative Artisans Found!**\n\nI found top-rated verified **${detectedTrade ?: primaryWorker.trade}s** for you:\n• **${primaryWorker.name}** (${primaryWorker.rating}★, ${primaryWorker.experienceYears} yrs experience)\n• Rate: ₹${primaryWorker.hourlyRate}/hr\n• Credentials: NSDC Certified & e-Shram Verified\n\nYou can view their full profile or book instantly below with zero platform markup."
            }
            return@withContext GharSathiMessage(
                text = text,
                isUser = false,
                language = if (isHindi) "hi" else "en",
                actionType = GharSathiActionType.RECOMMENDED_WORKERS,
                recommendedWorkers = matchingWorkers.take(3),
                targetTrade = detectedTrade ?: primaryWorker.trade
            )
        }

        if (isPaymentQuery) {
            val text = geminiResponseText ?: if (isHindi) {
                "💰 **पारदर्शी सहकारी दरें (Cooperative Rate Card)**\n\nश्रमिकनेक्ट / GHARgo में बिचौलियों का कोई कमीशन नहीं है:\n• **97% राशि** सीधे मेहनती कारीगर को जाती है।\n• **3% राशि** सामूहिक स्वास्थ्य एवं आपातकालीन कल्याण कोष (Welfare Pool) में जमा होती है।\n• स्विचबोर्ड रिपेयर मात्र **₹149** (निजी ऐप्स ₹299 लेती हैं)\n• नल लीकेज रिपेयर मात्र **₹200** (निजी ऐप्स ₹380 लेती हैं)\n• कोई छुपे शुल्क या सर्ज प्राइसिंग नहीं।"
            } else {
                "💰 **Transparent Cooperative Pricing**\n\nUnlike private aggregator apps that deduct 25-30% commissions, ShramConnect / GHARgo operates on a fair cooperative model:\n• **97% of your payment** goes directly to the artisan partner.\n• **3%** supports the democratic Worker Health & Accident Welfare Fund.\n• Standard Switchboard fix: **₹149** (Private apps charge ₹299)\n• Tap/Faucet leakage fix: **₹200** (Private apps charge ₹380)\n• Zero surge pricing, guaranteed upfront rates."
            }
            return@withContext GharSathiMessage(
                text = text,
                isUser = false,
                language = if (isHindi) "hi" else "en",
                actionType = GharSathiActionType.RATE_CARD,
                targetTrade = detectedTrade
            )
        }

        if (isCoopQuery) {
            val text = geminiResponseText ?: if (isHindi) {
                "🤝 **सहकारी मॉडल (Worker Cooperative) क्या है?**\n\n1. **कारीगर ही मालिक हैं**: यहाँ हर तकनीशियन के पास 1 वोट और हिस्सेदारी है (Democratic Governance)।\n2. **शून्य बिचौलिया शोषण**: कोई निजी कॉर्पोरेट कट नहीं है।\n3. **कल्याणकारी सुरक्षा**: हर काम से 3% वेलफेयर सेस कारीगरों के स्वास्थ्य बीमा, दुर्घटना सुरक्षा और वार्षिक लाभांश (Dividend) में जाता है।\n4. **100% पुलिस व e-Shram सत्यापित**: सभी कारीगर कौशल विकास (NSDC) प्रमाणित हैं।"
            } else {
                "🤝 **What is the ShramConnect Worker Cooperative?**\n\n1. **Worker-Owned**: Every technician owns equity shares and votes democratically (1 Worker = 1 Vote).\n2. **Zero Middlemen**: Private aggregators charge 25-30% cuts; here, 97% goes directly to the worker.\n3. **Safety & Medical Pool**: 3% builds a collective healthcare, accident insurance, and annual dividend pool.\n4. **100% Certified**: Background checked, police verified, e-Shram registered, and NSDC Skill India certified."
            }
            return@withContext GharSathiMessage(
                text = text,
                isUser = false,
                language = if (isHindi) "hi" else "en",
                actionType = GharSathiActionType.COOPERATIVE_TRANSPARENCY
            )
        }

        // Default conversational answer
        val defaultText = geminiResponseText ?: if (isHindi) {
            "नमस्ते! मैं आपका **घरसाथी** AI सहायक हूँ।\n\nमैं आपकी किस प्रकार सहायता कर सकता हूँ?\n• इलेक्ट्रीशियन, प्लम्बर, पेंटर या कारपेंटर खोजें\n• अपनी बुकिंग का स्टेटस और कारीगर की लोकेशन जानें\n• पारदर्शी रेट कार्ड और बचत समझें\n• आपातकालीन रिपेयर (Emergency SOS) बुक करें"
        } else {
            "Hello! I am **gharSathi**, your AI companion for ShramConnect.\n\nHow can I help you today?\n• Find verified local Electricians, Plumbers, Carpenters, or Appliance Techs\n• Track your current booking & OTP\n• Check transparent cooperative rate cards\n• Get 24/7 Emergency SOS repair service"
        }

        return@withContext GharSathiMessage(
            text = defaultText,
            isUser = false,
            language = if (isHindi) "hi" else "en",
            actionType = GharSathiActionType.GENERAL_CHAT
        )
    }

    private fun callGeminiApi(
        userInput: String,
        isHindi: Boolean,
        apiKey: String,
        allWorkers: List<WorkerEntity>,
        userBookings: List<BookingEntity>,
        detectedTrade: String?
    ): String? {
        val languageInstruction = if (isHindi) {
            "You MUST respond ONLY in fluent, polite, and natural Hindi (Devanagari script). Use warm Indian conversational tone."
        } else {
            "You MUST respond ONLY in clear, professional, and friendly English."
        }

        val workersContext = allWorkers.take(5).joinToString("; ") {
            "${it.name} (${it.trade}, rating ${it.rating}★, ₹${it.hourlyRate}/hr, exp ${it.experienceYears}y, locality ${it.locality})"
        }

        val bookingsContext = if (userBookings.isNotEmpty()) {
            userBookings.joinToString("; ") {
                "${it.serviceTitle} with ${it.workerName} on ${it.scheduledDate} ${it.scheduledTime}, status: ${it.status}, OTP: ${it.otpCode}"
            }
        } else {
            "No active bookings right now."
        }

        val systemPrompt = """
            You are 'gharSathi' (घरसाथी), an empathetic, smart, and friendly AI Assistant for 'ShramConnect' (GHARgo), a worker-owned home service cooperative in India.
            $languageInstruction
            
            Platform Context:
            - Model: Cooperative, 0% platform markup, 97% directly to artisan, 3% to worker medical welfare fund.
            - Services: Electrician, Plumber, Carpenter, Painter, Cleaner, Caregiver, Driver, Appliance Technician.
            - Sample Verified Workers: $workersContext
            - User's Bookings: $bookingsContext
            - Focus: Help users find services, verified workers, explain rate cards, check booking status, guide on cooperative ethics.
            - Keep your response friendly, concise, highly readable with bullet points where appropriate (under 120 words).
        """.trimIndent()

        val rootJson = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
            })
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", userInput)))
            }))
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("topP", 0.9)
                put("maxOutputTokens", 500)
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val body = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder().url(url).post(body).build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBodyString = response.body?.string() ?: return null
        val resJson = JSONObject(responseBodyString)
        val candidates = resJson.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null
        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null
        return parts.getJSONObject(0).optString("text")
    }

    private fun detectTrade(query: String): String? {
        return when {
            query.contains("electric") || query.contains("bijli") || query.contains("wiring") ||
                    query.contains("switch") || query.contains("fan") || query.contains("light") ||
                    query.contains("इलेक्ट्री") || query.contains("बिजली") || query.contains("पंखा") -> "Electrician"

            query.contains("plumb") || query.contains("pipe") || query.contains("leak") ||
                    query.contains("tap") || query.contains("tank") || query.contains("नल") ||
                    query.contains("प्लम्बर") || query.contains("पाइप") || query.contains("पानी") -> "Plumber"

            query.contains("carpent") || query.contains("wood") || query.contains("door") ||
                    query.contains("lock") || query.contains("table") || query.contains("बढ़ई") ||
                    query.contains("लकड़ी") || query.contains("दरवाजा") || query.contains("ताला") -> "Carpenter"

            query.contains("paint") || query.contains("color") || query.contains("wall") ||
                    query.contains("पेंट") || query.contains("रंग") || query.contains("दीवार") -> "Painter"

            query.contains("clean") || query.contains("scrub") || query.contains("maid") ||
                    query.contains("झाड़ू") || query.contains("सफाई") || query.contains("क्लीन") -> "Cleaner"

            query.contains("ac") || query.contains("refrigerator") || query.contains("fridge") ||
                    query.contains("appliance") || query.contains("tech") || query.contains("कूलर") ||
                    query.contains("फ्रिज") || query.contains("एसी") -> "Technician"

            query.contains("drive") || query.contains("car") || query.contains("ड्राइवर") ||
                    query.contains("गाड़ी") -> "Driver"

            query.contains("care") || query.contains("elder") || query.contains("nurse") ||
                    query.contains("बुजुर्ग") || query.contains("देखभाल") -> "Caregiver"

            query.contains("garden") || query.contains("grass") || query.contains("plant") ||
                    query.contains("माली") || query.contains("बगीचा") -> "Gardener"

            else -> null
        }
    }

    private fun findMatchingWorkersByKeywords(query: String, allWorkers: List<WorkerEntity>): List<WorkerEntity> {
        return allWorkers.filter { worker ->
            worker.name.contains(query, ignoreCase = true) ||
            worker.trade.contains(query, ignoreCase = true) ||
            worker.specializations.contains(query, ignoreCase = true) ||
            worker.locality.contains(query, ignoreCase = true)
        }
    }

    private fun isWorkerSearchQuery(query: String): Boolean {
        val keywords = listOf(
            "worker", "technician", "artisan", "hire", "find", "search", "who", "recommend",
            "कारीगर", "मिस्त्री", "खोजो", "चाहिए", "मदद", "सर्विस", "ढूंढो"
        )
        return keywords.any { query.contains(it) }
    }

    private fun isBookingStatusQuery(query: String): Boolean {
        val keywords = listOf(
            "status", "booking", "track", "otp", "where is", "arrived", "when", "order",
            "बुकिंग", "स्थिति", "स्टेटस", "कहाँ", "ओटीपी", "पहुंचा", "कब आएगा"
        )
        return keywords.any { query.contains(it) }
    }

    private fun isPaymentOrRateQuery(query: String): Boolean {
        val keywords = listOf(
            "rate", "price", "cost", "fee", "how much", "cheap", "charge", "payment", "commission", "rate card",
            "दाम", "रेट", "खर्च", "पैसे", "फीस", "कमीशन", "कितना", "भुगतान"
        )
        return keywords.any { query.contains(it) }
    }

    private fun isCooperativeQuery(query: String): Boolean {
        val keywords = listOf(
            "cooperative", "coop", "sahakari", "why shramconnect", "what is shram", "welfare", "shares",
            "democratic", "trust", "verified", "background",
            "सहकारी", "कमीशन क्यों नहीं", "वेलफेयर", "भरोसा", "सत्यापन", "श्रमिक", "क्या है"
        )
        return keywords.any { query.contains(it) }
    }

    private fun isEmergencyQuery(query: String): Boolean {
        val keywords = listOf(
            "emergency", "urgent", "sos", "danger", "burst", "shock", "fire", "spark", "immediate",
            "आपातकाल", "तत्काल", "तुरंत", "खतरा", "शॉर्ट सर्किट", "लीक"
        )
        return keywords.any { query.contains(it) }
    }

    private fun translateStatusToHindi(status: String): String {
        return when (status) {
            "CONFIRMED" -> "पुष्ट (कारीगर असाइन हो गया)"
            "WORKER_ASSIGNED" -> "कारीगर रवाना होने को तैयार"
            "WORKER_ARRIVING" -> "कारीगर रास्ते में है"
            "SERVICE_STARTED" -> "कार्य प्रगति पर है"
            "COMPLETED" -> "सफलतापूर्वक संपन्न"
            else -> status
        }
    }
}
