package benchmark

interface Api {
    fun call(): Int
}

class NormalApiImpl : Api {
    override fun call(): Int {
        return 0
    }
}

class ExceptionApiImpl: Api {
    override fun call(): Int {
        throw RuntimeException()
    }
}

