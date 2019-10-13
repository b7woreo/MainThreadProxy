package util

import java.util.concurrent.CountDownLatch

internal class Feature<T : Any> {

    private val latch = CountDownLatch(1)
    private var value: T? = null

    fun set(value: T) {
        synchronized(this) {
            if (this.value != null) throw AssertionError("only call once")
            this.value = value
        }
        latch.countDown()
    }

    fun blockingGet(): T {
        latch.await()
        synchronized(this) {
            return value!!
        }
    }
}
