package util

import android.os.Handler
import android.os.Looper
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

fun <T : Any> T.mainThreadProxy(): T {
    return MainThreadProxy.create(this)
}

object MainThreadProxy {

    private val mainHandler = Handler(Looper.getMainLooper())

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> create(instance: T): T {
        return Proxy.newProxyInstance(
            instance::class.java.classLoader,
            instance::class.java.interfaces
        ) { _, method, args ->
            runOnMainThreadSync {
                try {
                    if (args != null) {
                        method.invoke(instance, *args)
                    } else {
                        method.invoke(instance)
                    }
                } catch (e: InvocationTargetException) {
                    throw e.targetException
                } catch (e: Throwable) {
                    throw AssertionError(e)
                }
            }.getOrThrow()
        } as T
    }

    private inline fun <T> runOnMainThreadSync(crossinline block: () -> T): Result<T> {
        return if (Thread.currentThread() == Looper.getMainLooper().thread) {
            return runCatching(block)
        } else {
            val feature = Feature<Result<T>>()
            mainHandler.post {
                val result = runCatching(block)
                feature.set(result)
            }
            feature.blockingGet()
        }
    }

}
