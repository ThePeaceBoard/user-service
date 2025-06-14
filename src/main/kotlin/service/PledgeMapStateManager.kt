package org.example.service

import mu.KotlinLogging
import org.example.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class PledgeMapStateManager(
    private val userRepository: UserRepository
) {
    private val pledgeCountByCountry = ConcurrentHashMap<String, AtomicLong>()
    private val logger = KotlinLogging.logger {}

    init {
        initializePledgeCounts()
    }

    // Initialize state from the database at startup
    private fun initializePledgeCounts() {
        userRepository.countPledgesByCountryAsync()
            .subscribe { pledgeCountDto ->
                val countryCode = pledgeCountDto.countryCode ?: "UNKNOWN" // Handle nullable case
                pledgeCountByCountry[countryCode] = AtomicLong(pledgeCountDto.count)
            }
    }

    // Increment pledge count for a given country
    fun incrementPledgeCount(countryCode: String) {
        pledgeCountByCountry.computeIfAbsent(countryCode) { AtomicLong(0) }.incrementAndGet()
    }

    // Return the current state data as a map
    fun getStateData(): Map<String, Long> {
        return pledgeCountByCountry.mapValues { it.value.get() }
    }

    // Refresh pledge counts from the database
    fun refreshPledgeCounts(): Mono<Void> {
        return userRepository.countPledgesByCountryAsync()
            .doOnNext { pledgeCountDto ->
                val countryCode = pledgeCountDto.countryCode ?: "UNKNOWN" // Handle nullable case
                pledgeCountByCountry[countryCode] = AtomicLong(pledgeCountDto.count)
            }
            .then()
    }
}
