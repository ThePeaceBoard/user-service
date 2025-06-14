package org.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.config.Customizer.withDefaults

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {
    
    @Bean fun userEventsExchange() = TopicExchange(exchange, true, false)
    @Bean fun userServiceQueue() = Queue("user-service.queue", true)
    @Bean fun binding() = BindingBuilder.bind(userServiceQueue()).to(userEventsExchange()).with("user.*")

    @Bean
    fun securityConfig(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { csrfSpec -> csrfSpec.disable() }  // Disable CSRF protection
            .authorizeExchange { authorize ->
                authorize

                    // UserController endpoints are protected, require authentication
                    .pathMatchers("/api/users/**").authenticated()

                    // DataController endpoints are also protected, require authentication
                    .pathMatchers("/api/data/**").authenticated()

                    // All other paths must be authenticated
                    .anyExchange().authenticated()
            }
            .httpBasic(withDefaults())  // Enable HTTP Basic Authentication
            .build()
    }

    @Bean
    fun userDetailsService(): MapReactiveUserDetailsService {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("USER")
            .build()

        return MapReactiveUserDetailsService(user)
    }
}
