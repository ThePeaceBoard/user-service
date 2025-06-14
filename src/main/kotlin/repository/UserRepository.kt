package org.example.repository

import org.example.dto.PledgeCountDto
import org.example.entity.User
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<User, Long> {

    // Find user by email using a custom query
    @Query("SELECT * FROM USERS WHERE email = :email")
    fun findByEmail(email: String): Mono<User?>

    // Find user by phone number using a custom query
    @Query("SELECT * FROM USERS WHERE phone_number = :phoneNumber")
    fun findByPhoneNumber(phoneNumber: String): Mono<User?>

    // Corrected query to count pledges by country and map it to a tuple or DTO
    @Query("SELECT country_code AS countryCode, COUNT(*) AS count FROM USERS WHERE has_pledged = true GROUP BY country_code")
    fun countPledgesByCountryAsync(): Flux<PledgeCountDto>
}
