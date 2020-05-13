package com.example.snapventuremultiplayer.repository.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object User {
    @Entity(tableName = "user")
    data class LoginResponse(
            @NonNull
            @PrimaryKey(autoGenerate = true)
            @ColumnInfo(name = "user_id")
            var id: Int = 0,
            @ColumnInfo(name = "user_name")
            var name: String = "",
            @ColumnInfo(name = "user_nim")
            var nim: String = "",
            @ColumnInfo(name = "user_email")
            var email: String = "",
            @ColumnInfo(name = "user_password")
            var password: String = "",
            @ColumnInfo(name = "user_division")
            var division: String = ""
    )

    data class UserLogin(
            @ColumnInfo(name = "login_nim")
            var nim: String,
            @ColumnInfo(name = "login_password")
            var password: String
    )

    data class UserRegister(
            @ColumnInfo(name = "register_name")
            var name: String,
            @ColumnInfo(name = "register_email")
            var email: String
    )
}
