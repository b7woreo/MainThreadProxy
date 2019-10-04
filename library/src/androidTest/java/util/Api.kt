package util

interface Api {
   fun call():String
}

class ApiException(): RuntimeException()
