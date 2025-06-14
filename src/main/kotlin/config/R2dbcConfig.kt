package org.example.config

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration
import dev.miku.r2dbc.mysql.MySqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import java.time.Duration
import java.time.ZoneId

@Configuration
class R2dbcConfiguration : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        // Fetch values from environment variables injected by Kubernetes
        val host = System.getenv("DB_HOST") ?: "localhost"
        val port = System.getenv("DB_PORT")?.toInt() ?: 3306
        val username = System.getenv("DB_USERNAME") ?: "root"
        val password = System.getenv("DB_PASSWORD") ?: ""
        val database = System.getenv("DB_DATABASE") ?: "USERS"

        return MySqlConnectionFactory.from(
            MySqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .username(username)
                .password(password)
                .database(database)
                .serverZoneId(ZoneId.of("UTC"))
                .connectTimeout(Duration.ofSeconds(3))
                .useServerPrepareStatement()
                .build()
        )
    }
}