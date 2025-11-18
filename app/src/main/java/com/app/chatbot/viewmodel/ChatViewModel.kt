package com.app.chatbot.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.chatbot.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages

    fun sendMessage(message: String) {
        addMessage(message, isUser = true, side = "right", timeStamp = System.currentTimeMillis())
        addMessage("", isUser = false, side = "left", isLoading = true, timeStamp = System.currentTimeMillis())

        viewModelScope.launch {
            val response = callGemini(message, "AIzaSyDDw3rwvnHDH2ry57fAC9Fgo0pUqvis0fc")
            removeLastMessage()
            addMessage(response, isUser = false, side = "left", isLoading = false, timeStamp = System.currentTimeMillis())
        }
    }

    private fun addMessage(text: String, isUser: Boolean, side: String, isLoading: Boolean = false, timeStamp: Long) {
        _messages.value = _messages.value + Message(text, isUser, side, isLoading, timeStamp)
    }

    private fun removeLastMessage() {
        _messages.value = _messages.value.dropLast(1)
    }

    private suspend fun callGemini(prompt: String, apiKey: String): String = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val json = """
            {
              "contents":[
                {
                  "parts":[
                    { "text": "${prompt.replace("\"", "\\\"")} " }
                  ]
                }
              ]
            }
        """.trimIndent()

            val body = json.toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string() ?: return@withContext "No response body"
                if (!response.isSuccessful) {
                    Log.e("ChatViewModel", "Error response: $raw")
                    return@withContext "Sorry, I encountered an error: ${response.code} - ${response.message}"
                }
                try {
                    val obj = JSONObject(raw)
                    val candidates = obj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            return@withContext text
                        }
                    }
                    return@withContext "Sorry, I couldn't parse the response from the AI."
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "JSON parsing error: ${e.message}", e)
                    return@withContext "Sorry, I couldn't parse the response from the AI."
                }
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Network error: ${e.message}", e)
            return@withContext "Sorry, I couldn't connect to the AI service. Please check your internet connection."
        }
    }
}
