package org.example.controller

import mu.KotlinLogging
import org.example.service.PledgeMapStateManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/data")
class DataController(
    private val pledgeMapStateManager: PledgeMapStateManager
) {
    private val logger = KotlinLogging.logger {}

    // Endpoint to get current pledge counts grouped by country
    @GetMapping("/pledge-counts")
    fun getPledgeCountsByCountry(): Mono<Map<String, Long>> {
        logger.info { "Fetching pledge counts grouped by country." }

        return Mono.just(pledgeMapStateManager.getStateData())
            .doOnSuccess { counts -> logger.info { "Successfully retrieved pledge counts: $counts" } }
            .doOnError { error -> logger.error(error) { "Failed to retrieve pledge counts." } }
    }

    // Optional endpoint to refresh the pledge counts from the database
    @PutMapping("/refresh-pledge-counts")
    fun refreshPledgeCounts(): Mono<Void> {
        logger.info { "Refreshing pledge counts from the database." }

        return pledgeMapStateManager.refreshPledgeCounts()
            .doOnSuccess { logger.info { "Pledge counts successfully refreshed from the database." } }
            .doOnError { error -> logger.error(error) { "Failed to refresh pledge counts from the database." } }
    }
}
