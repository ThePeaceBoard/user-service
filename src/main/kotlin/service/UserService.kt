package org.example.service

import mu.KotlinLogging
import org.example.entity.User
import org.example.events.PledgeEventPublisher
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val pledgeEventPublisher: PledgeEventPublisher
) {
    private val logger = KotlinLogging.logger {}

    // Enrich user profile with OAuth data (existing method for OAuth)
    fun createUserProfileWithOauthData(
        email: String,
        firstName: String?,
        lastName: String?,
        countryCode: String?
    ): Mono<User> {
        logger.info { "Enriching user profile with OAuth data for user: $email" }

        return findUser(email)
            .switchIfEmpty(
                // Create a new user if not found
                Mono.defer {
                    logger.info { "User not found, creating new user with email: $email" }
                    val newUser = User(
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        isOauthVerified = true,
                        countryCode = countryCode
                    )
                    saveUser(newUser)
                }
            )
            .flatMap { user ->
                // Enrich existing user profile with OAuth data
                user.firstName = firstName ?: user.firstName
                user.lastName = lastName ?: user.lastName
                user.isOauthVerified = true  // Mark the user as OAuth verified
                user.countryCode = countryCode ?: user.countryCode
                saveUser(user)
            }
            .doOnSuccess { user ->
                logger.info { "User profile enriched successfully for user: ${user.email}" }
            }
            .onErrorResume { error ->
                logger.error(error) { "Failed to enrich user profile for user: $email" }
                Mono.error(error)
            }
    }

    fun createUser(email: String): Mono<User> {
        logger.info { "Attempting to create user with email: $email" }

        return userRepository.findByEmail(email)
            .flatMap { existingUser ->
                logger.info { "findByEmail returned: $existingUser" }
                if (existingUser != null) {
                    logger.warn { "User with email $email already exists." }
                    Mono.error(RuntimeException("User with email $email already exists."))
                } else {saveUser(User(email = email))}
            }
            .onErrorResume { error ->
                logger.error(error) { "Failed to create user with email: $email" }
                Mono.error(error)
            }
    }

    fun userPledged(email: String, countryCode: String): Mono<User> {
        logger.info { "User with email: $email is attempting to pledge for countryCode: $countryCode" }

        return findUser(email)
            .flatMap { user ->
                if (!user.hasPledged) {
                    logger.info { "User with email: $email is pledging for the first time." }
                    user.hasPledged = true

                    saveUser(user)
                        .flatMap {
                            logger.info { "User with email: $email successfully pledged." }
                            pledgeEventPublisher.publishPledgeEvent(email, countryCode)
                        }
                        .thenReturn(user)  // Return the user upon success
                } else {
                    logger.warn { "User with email: $email has already pledged." }
                    Mono.error(RuntimeException("User has already pledged."))  // Return error if already pledged
                }
            }
            .onErrorResume { error ->
                logger.error(error) { "Failed to process pledge for user with email: $email" }
                Mono.error(error)  // Return Mono.error with the caught error
            }
    }


    // Verifies OTP and logs actions/errors
    fun verifyOtp(email: String, newPhoneNumber: String): Mono<User> {
        logger.info { "Verifying OTP for user with email: $email" }
        return findUser(email)
            .flatMap { user ->
                user.isOtpVerified = true
                user.phoneNumber = newPhoneNumber
                saveUser(user)
            }
            .doOnSuccess { logger.info { "OTP verified and phone number updated for user: $email" } }
            .onErrorResume { error ->
                logger.error(error) { "Failed to verify OTP for user: $email" }
                Mono.error(error)
            }
    }

    // Verifies OAuth and logs actions/errors
    fun verifyOauth(email: String): Mono<User> {
        logger.info { "Verifying OAuth for user with email: $email" }
        return findUser(email)
            .flatMap { user ->
                user.isOauthVerified = true
                saveUser(user)
            }
            .doOnSuccess { logger.info { "OAuth verified for user: $email" } }
            .onErrorResume { error ->
                logger.error(error) { "Failed to verify OAuth for user: $email" }
                Mono.error(error)
            }
    }

    // Connects a Seeder address to the user profile, logs actions/errors
    fun connectSeederAddress(email: String, seederAddress: String): Mono<User> {
        logger.info { "Connecting Seeder address for user with email: $email" }
        return findUser(email)
            .flatMap { user ->
                user.seederAddress = seederAddress
                saveUser(user)
            }
            .doOnSuccess { logger.info { "Seeder address connected for user: $email" } }
            .onErrorResume { error ->
                logger.error(error) { "Failed to connect Seeder address for user: $email" }
                Mono.error(error)
            }
    }

    // Retrieves a user by email, logs successful retrieval
    fun getUserByEmail(email: String): Mono<User> {
        logger.info { "Fetching user by email: $email" }
        return findUser(email)
            .doOnSuccess { logger.info { "User with email: $email retrieved successfully." } }
            .onErrorResume { error ->
                logger.error(error) { "Failed to retrieve user with email: $email" }
                Mono.error(error)
            }
    }

    // Private utility functions

    private fun findUser(email: String): Mono<User> {
        logger.info { "Finding user with email: $email" }
        return userRepository.findByEmail(email)
            .flatMap { user ->
                if (user != null) {
                    Mono.just(user)
                } else {
                    logger.warn { "User with email: $email not found." }
                    Mono.error(RuntimeException("User with email $email not found."))
                }
            }
            .onErrorResume { error ->
                logger.error(error) { "Error finding user with email: $email" }
                Mono.error(error)
            }
    }

    private fun saveUser(user: User): Mono<User> {
        logger.info { "Saving user with email: ${user.email}" }
        return userRepository.save(user)
            .doOnSuccess { logger.info { "User with email: ${user.email} saved successfully." } }
            .onErrorResume { error ->
                logger.error(error) { "Failed to save user with email: ${user.email}" }
                Mono.error(error)
            }
    }
}
