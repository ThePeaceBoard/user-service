plugins {
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("dev.miku:r2dbc-mysql:0.8.2.RELEASE")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-reactor-netty") // Reactive web support
    implementation ("org.flywaydb:flyway-core:8.0.0")

    // Kotlin Coroutines Core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

    // Resilience4j dependencies for Circuit Breaker and Retry
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.0.2")
    implementation("io.github.resilience4j:resilience4j-reactor:2.0.2")  // WebFlux and Reactor support

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")

    implementation("com.twilio.sdk:twilio:10.5.0")
//    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Michael Bull's Result Monad
    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.16")

    // Test dependencies (optional for now since testing isn't a priority)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("org.example.UserServiceApplicationKt") // Replace with the correct main class
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
