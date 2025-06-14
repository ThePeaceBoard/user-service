package org.example.events

import org.example.service.PledgeMapStateManager
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PledgeEventListener(
    private val pledgeMapStateManager: PledgeMapStateManager
) {
    @EventListener
    fun handlePledgeEvent(event: PledgeEvent) {
        pledgeMapStateManager.incrementPledgeCount(event.countryCode)
    }
}

class PledgeEvent(source: Any, val email: String, val countryCode: String) : ApplicationEvent(source)