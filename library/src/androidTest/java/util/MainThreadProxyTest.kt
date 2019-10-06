package util

import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainThreadProxyTest {

    @Test
    fun callOnMainThread() = InstrumentationRegistry.getInstrumentation().runOnMainSync {
        assertTrue(Looper.getMainLooper().isCurrentThread)
        callSuccess()
        callFailure()
    }

    @Test
    fun callNotOnMainThread() {
        assertFalse(Looper.getMainLooper().isCurrentThread)
        callSuccess()
        callFailure()
    }

    private fun callSuccess() {
        var runThread: Thread? = null
        val instance = object : Api {
            override fun call(): String {
                runThread = Thread.currentThread()
                return "test"
            }
        }

        val proxy = instance.mainThreadProxy<Api>()
        val result = proxy.call()

        assertNotNull(runThread)
        assertEquals(Looper.getMainLooper().thread, runThread)
        assertEquals("test", result)
    }

    private fun callFailure() {
        var runThread: Thread? = null
        val instance = object : Api {
            override fun call(): String {
                runThread = Thread.currentThread()
                throw ApiException()
            }
        }

        val proxy = instance.mainThreadProxy<Api>()
        try {
            proxy.call()
            fail()
        } catch (throwable: Throwable) {
            assertNotNull(runThread)
            assertEquals(Looper.getMainLooper().thread, runThread)
            assertEquals(ApiException::class, throwable::class)
            return
        }
    }

}
