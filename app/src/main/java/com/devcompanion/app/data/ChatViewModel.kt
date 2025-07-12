package com.devcompanion.app.ui.chat

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.devcompanion.app.data.ChatMessage
import com.devcompanion.app.data.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application.applicationContext)

    val chatMessages: LiveData<MutableList<ChatMessage>> = repository.chatMessages

    fun sendUserMessage(text: String) {
        val userMessage = ChatMessage(isUser = true, message = text)
        repository.addMessage(userMessage)

        // Simulated AI reply (replace with your AI logic)
        viewModelScope.launch(Dispatchers.IO) {
            val reply = "You said: $text"
            val aiMessage = ChatMessage(isUser = false, message = reply)
            repository.addMessage(aiMessage)
        }
    }

    fun sendUserImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "img_${System.currentTimeMillis()}.png"
            val path = repository.saveImage(bitmap, fileName)
            val userImageMessage = ChatMessage(isUser = true, message = "Sent an image", imagePath = path)
            repository.addMessage(userImageMessage)

            // Simulated AI reply for image
            val aiReply = "Image received and analyzed."
            val aiMessage = ChatMessage(isUser = false, message = aiReply)
            repository.addMessage(aiMessage)
        }
    }
}