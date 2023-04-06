package dev.souravdas.hush.others

/**
 * Created by Sourav
 * On 3/26/2023 7:23 PM
 * For Currency Converter
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
    fun peekContent(): T = content
}