package org.example.dto

data class PledgeCountDto(
    val countryCode: String?,  // Make countryCode nullable
    val count: Long
)
