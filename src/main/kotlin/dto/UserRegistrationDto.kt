package org.example.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRegistrationDto(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email should be valid")
    val email: String,
)
