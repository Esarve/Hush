package dev.souravdas.hush.others

/**
 * Created by Sourav
 * On 4/12/2023 10:43 AM
 * For Hush!
 */
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
}
