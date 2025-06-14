package org.example.error

import org.springframework.http.ResponseEntity

open class ServiceError(
    val message: String,
    val throwable: Throwable? = null
) {
    override fun toString(): String {
        return if (throwable != null) {
            "$message - Caused by: ${throwable.message}"
        } else {
            message
        }
    }

    // Wrap failure into a ResponseEntity
    fun <T> toResponseEntity(): ResponseEntity<T> {
        return ResponseEntity.badRequest().body(null) // You can customize the body if needed
    }
}

sealed class ResponseWrapper<T> {
    data class Success<T>(val body: ResponseEntity<T>) : ResponseWrapper<T>()
    data class Failure(val error: ServiceError) : ResponseWrapper<Nothing>()

    companion object {
        fun <T> success(body: T): ResponseWrapper<T> {
            return Success(ResponseEntity.ok(body))
        }

        fun failure(error: ServiceError): ResponseWrapper<Nothing> {
            return Failure(error)
        }
    }

    // Convert the wrapper into a ResponseEntity
    fun toResponseEntity(): ResponseEntity<*> {
        return when (this) {
            is Success -> this.body
            is Failure -> this.error.toResponseEntity<Any>()
        }
    }
}

