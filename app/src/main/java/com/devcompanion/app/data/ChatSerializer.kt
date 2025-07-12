package com.devcompanion.app.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ChatSerializer {
    private val gson = Gson()

    fun serialize(messages: List<ChatMessage>): String {
        return gson.toJson(messages)
    }

    fun deserialize(json: String): List<ChatMessage> {
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(json, type)
    }
}