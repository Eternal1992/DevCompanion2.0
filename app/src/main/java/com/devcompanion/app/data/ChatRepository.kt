package com.devcompanion.app.data

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileOutputStream

data class ChatMessage(
    val isUser: Boolean,
    val message: String,
    val imagePath: String? = null
)

class ChatRepository(private val context: Context) {
    private val _chatMessages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val chatMessages: LiveData<MutableList<ChatMessage>> = _chatMessages

    private val chatFile = File(context.filesDir, "chat_log.json")

    init {
        loadChat()
    }

    fun addMessage(message: ChatMessage) {
        _chatMessages.value?.add(message)
        _chatMessages.postValue(_chatMessages.value)
        saveChat()
    }

    private fun saveChat() {
        val jsonString = ChatSerializer.serialize(_chatMessages.value ?: listOf())
        chatFile.writeText(jsonString)
    }

    private fun loadChat() {
        if (chatFile.exists()) {
            val jsonString = chatFile.readText()
            val messages = ChatSerializer.deserialize(jsonString)
            _chatMessages.postValue(messages.toMutableList())
        }
    }

    // Save image locally and return the saved file path
    fun saveImage(bitmap: Bitmap, imageName: String): String? {
        return try {
            val imageFile = File(context.filesDir, imageName)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}