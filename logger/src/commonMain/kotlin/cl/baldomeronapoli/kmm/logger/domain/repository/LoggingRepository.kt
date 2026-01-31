package cl.baldomeronapoli.kmm.logger.domain.repository

interface LoggingRepository {
    suspend fun crash(throwable: Throwable)

    // Verbose - información muy detallada
    suspend fun v(tag: String, message: String, throwable: Throwable? = null)

    // Debug - información de depuración
    suspend fun d(tag: String, message: String, throwable: Throwable? = null)

    // Info - información general
    suspend fun i(tag: String, message: String, throwable: Throwable? = null)

    // Warning - advertencias
    suspend fun w(tag: String, message: String, throwable: Throwable? = null)

    // Error - errores
    suspend fun e(tag: String, message: String, throwable: Throwable? = null)

    // WTF (What a Terrible Failure) - errores críticos
    suspend fun wtf(tag: String, message: String, throwable: Throwable? = null)
}