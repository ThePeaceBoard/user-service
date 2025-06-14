package org.example.dto

data class OauthProfileRequest(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val countryCode: String?,
)
