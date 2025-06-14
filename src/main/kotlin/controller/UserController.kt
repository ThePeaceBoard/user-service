package org.example.controller

import jakarta.validation.Valid
import mu.KotlinLogging
import org.example.dto.OauthProfileRequest
import org.example.dto.PledgeRequest
import org.example.dto.UserRegistrationDto
import org.example.entity.User
import org.example.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
@Validated
class UserController(
    private val userService: UserService
) {
    private val logger = KotlinLogging.logger {}

    // Pledge endpoint
    @PostMapping("/pledge")
    fun pledgeForUser(@RequestBody pledgeRequest: PledgeRequest): Mono<ResponseEntity<User>> {
        return userService.userPledged(pledgeRequest.email, pledgeRequest.countryCode)
            .map { ResponseEntity.ok(it) }  // Return 200 OK with the pledged user data
            .onErrorResume { throwable ->
                Mono.just(ResponseEntity.badRequest().body(null))  // Return 400 Bad Request if any error occurs
            }
    }

    // Endpoint to enrich user profile with OAuth data
    @PostMapping("/create")
    fun enrichUserProfileWithOauthData(@RequestBody oauthProfile: OauthProfileRequest): Mono<User> {
        return userService.createUserProfileWithOauthData(
            oauthProfile.email,
            oauthProfile.firstName,
            oauthProfile.lastName,
            oauthProfile.countryCode,
        )
    }

    @PostMapping("/verify/oauth")
    fun verifyOauth(@RequestParam email: String): Mono<ResponseEntity<String>> {
        return userService.verifyOauth(email)
            .map { ResponseEntity.ok("OAuth verified successfully.") }
            .onErrorResume { throwable -> Mono.just(ResponseEntity.badRequest().body(throwable.message)) }
    }

    @PostMapping("/connect-wallet")
    fun connectWallet(@RequestParam email: String, @RequestParam seederAddress: String): Mono<ResponseEntity<String>> {
        return userService.connectSeederAddress(email, seederAddress)
            .map { ResponseEntity.ok("Wallet connected successfully.") }
            .onErrorResume { throwable -> Mono.just(ResponseEntity.badRequest().body(throwable.message)) }
    }

    @GetMapping("/{email}")
    fun getUser(@PathVariable email: String): Mono<ResponseEntity<User>> {
        return userService.getUserByEmail(email)
            .map { ResponseEntity.ok(it) }
            .onErrorResume { throwable -> Mono.just(ResponseEntity.badRequest().body(null)) }
    }
}
