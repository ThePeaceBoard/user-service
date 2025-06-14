package org.example.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("USERS")  // R2DBC-specific annotation to map the entity to a table
data class User(

    @Id  // R2DBC annotation for identifying the primary key
    val id: Long = 0,

    var firstName: String? = null,  // New field for first name

    var lastName: String? = null,   // New field for last name

    val email: String,

    var phoneNumber: String? = null,

    var isOtpVerified: Boolean = false,

    var isOauthVerified: Boolean = false,

    var seederAddress: String? = null,

    var countryCode: String? = null,

    var state: String? = null,

    var hasPledged: Boolean = false,

    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun isFullyVerified(): Boolean = isOtpVerified && isOauthVerified
}
