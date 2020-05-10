package com.example.snapventuremultiplayer.utils

import android.Manifest

class Constants {

    companion object {

        // Timeout / Delay
        const val AUTH_TIMEOUT: Long = 5000L
        const val SPLASH_SCREEN_DELAY: Long = 2000L

        // Firebase Collection
        const val ROOM_COLLECTION = "room"
        const val USERS_COLLECTION = "users"

        // Firestore User Document Column
        const val NIM = "nim"

        // Permissions
        const val CAMERA_REQUEST_CODE_PERMISSIONS = 10

        val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        // Intent
        const val EXTRA_ROOM_DATA = "EXTRA_ROOM_DATA"

    }
}
