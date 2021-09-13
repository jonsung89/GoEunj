package com.jonsung.goeunj.data

import com.jonsung.goeunj.data.LoadingState.*

/**
 * The state of data being loaded asynchronously.
 * This class is generic ('T' is represented by 'PayloadType'), allowing you to specify whatever
 * data payload type you'd like
 *
 * @property requestInitiatorId An ID representing the resource in the UI that kicked off the
 * loading of data. Think of this as a "return address"
 */
sealed class LoadingState<out PayloadType>(open val requestInitiatorId: String) {
    /**
     * The requested data has begun and hasn't yet completed loading
     */
    data class Loading(override val requestInitiatorId: String) : LoadingState<Nothing>(requestInitiatorId)

    /**
     * The requested data has successfully completed loading
     * @property payload – The data you wanted to load
     */
    data class Loaded<PayloadType>(
        val payload: PayloadType,
        override val requestInitiatorId: String) : LoadingState<PayloadType>(requestInitiatorId)

    /**
     * The attempt to load data was unsuccessful
     */
    data class Failed<PayloadType>(
        val errorMessage: String,
        override val requestInitiatorId: String,
        val title: String? = null) : LoadingState<PayloadType>(requestInitiatorId)
}

inline fun <T, U> LoadingState<T>.map(f: (T) -> U): LoadingState<U> = when (this) {
    is Loading -> this
    is Loaded<T> -> Loaded(f(payload), requestInitiatorId)
    is Failed -> (this as LoadingState<U>)
}

inline fun <T, U> LoadingState<T>.mapFailure(f: (message: String, title: String?) -> U): LoadingState<T> = when (this) {
    is Loading -> this
    is Loaded<T> -> this
    is Failed<T> -> {
        f(errorMessage, title); this
    }
}

inline fun <T> LoadingState<T>.mapLoading(f: (Unit) -> Unit): LoadingState<T> = when (this) {
    is Loading -> {
        f(Unit); this
    }
    is Loaded -> this
    is Failed -> this
}
