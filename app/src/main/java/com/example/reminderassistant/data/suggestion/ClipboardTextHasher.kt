package com.example.reminderassistant.data.suggestion

import java.security.MessageDigest
import javax.inject.Inject

class ClipboardTextHasher @Inject constructor() {
    fun hash(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
