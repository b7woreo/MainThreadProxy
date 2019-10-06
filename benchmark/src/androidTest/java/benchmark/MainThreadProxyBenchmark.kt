package benchmark

import android.os.Looper
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import util.mainThreadProxy

@RunWith(AndroidJUnit4::class)
class MainThreadProxyBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var normalOrigin: Api
    private lateinit var normalProxy: Api

    private lateinit var exceptionOrigin: Api
    private lateinit var exceptionProxy: Api

    @Before
    fun setup() {
        normalOrigin = NormalApiImpl()
        normalProxy = normalOrigin.mainThreadProxy()

        exceptionOrigin = ExceptionApiImpl()
        exceptionProxy = exceptionOrigin.mainThreadProxy()
    }

    @Test
    fun origin_call() {
        benchmarkRule.measureRepeated {
            normalOrigin.call()
        }
    }

    @Test
    fun origin_throw() {
        benchmarkRule.measureRepeated {
            runCatching { exceptionOrigin.call() }
        }
    }

    @Test
    fun proxy_call_onMainThread() = InstrumentationRegistry.getInstrumentation().runOnMainSync {
        assertTrue(Looper.getMainLooper().isCurrentThread)
        benchmarkRule.measureRepeated {
            normalProxy.call()
        }
    }

    @Test
    fun proxy_call_notOnMainThread() {
        assertFalse(Looper.getMainLooper().isCurrentThread)
        benchmarkRule.measureRepeated {
            normalProxy.call()
        }
    }

    @Test
    fun proxy_throw_onMainThread() = InstrumentationRegistry.getInstrumentation().runOnMainSync {
        assertTrue(Looper.getMainLooper().isCurrentThread)
        benchmarkRule.measureRepeated {
            runCatching { exceptionProxy.call() }
        }
    }

    @Test
    fun proxy_throw_notOnMainThread() {
        assertFalse(Looper.getMainLooper().isCurrentThread)
        benchmarkRule.measureRepeated {
            runCatching { exceptionProxy.call() }
        }
    }
}
