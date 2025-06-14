package org.example.events

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PledgeEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publishPledgeEvent(email: String, countryCode: String): Mono<Void> {
        return Mono.fromRunnable {
            val event = PledgeEvent(this, email, countryCode)
            applicationEventPublisher.publishEvent(event)
        }
    }
}
