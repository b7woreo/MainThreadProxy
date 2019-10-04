package util

import android.os.Handler
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainThreadProxyTest {

    private lateinit var mainThread: Thread
    private lateinit var mainHandler: Handler

    @Before
    fun setup() {
        mainThread = Looper.getMainLooper().thread
        mainHandler = Handler(Looper.getMainLooper())
    }

    @Test
    fun callOnMainThread() = InstrumentationRegistry.getInstrumentation().runOnMainSync {
        assertEquals(mainThread, Thread.currentThread())
        callSuccess()
        callFailure()
    }

    @Test
    fun callNotOnMainThread() {
        assertNotEquals(mainThread, Thread.currentThread())
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
        assertEquals(mainThread, runThread)
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
        } catch (throwable: Throwable) {
            assertNotNull(runThread)
            assertEquals(mainThread, runThread)
            assertEquals(ApiException::class, throwable::class)
            return
        }
        fail("not throw exception")
    }

}
