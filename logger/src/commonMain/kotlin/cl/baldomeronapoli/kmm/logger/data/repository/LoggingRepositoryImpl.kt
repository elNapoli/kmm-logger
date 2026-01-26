package cl.baldomeronapoli.kmm.logger.data.repository

import cl.baldomeronapoli.kmm.base.domain.repository.LoggingRepository
import cl.baldomeronapoli.kmm.logger.data.datasource.LogDataSource
import cl.baldomeronapoli.kmm.logger.domain.model.LogLevel

/**
 * Implementación del repositorio de logging siguiendo Clean Architecture.
 * Coordina múltiples datasources (Napier, Crashlytics, etc.) para enviar logs a diferentes destinos.
 *
 * @param dataSources Lista de datasources configurados
 * @param minLogLevel Nivel mínimo global de logging
 */
class LoggingRepositoryImpl(
    private val dataSources: List<LogDataSource>,
    private val minLogLevel: LogLevel
) : LoggingRepository {

    /**
     * Loggea una excepción a todos los datasources habilitados.
     * Usado automáticamente por ExceptionHandler en UseCases.
     */
    override suspend fun logException(throwable: Throwable) {
        log(
            level = LogLevel.ERROR,
            tag = throwable::class.simpleName ?: "Exception",
            message = throwable.message ?: "Exception without message",
            throwable = throwable
        )
    }

    /**
     * Loggea un mensaje con nivel específico.
     * Útil para logging manual en la aplicación.
     *
     * @param level Nivel de severidad
     * @param tag Tag/categoría del log
     * @param message Mensaje a loggear
     * @param throwable Exception opcional
     */
    fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable? = null
    ) {
        // Verificar si debe loggearse según el nivel mínimo
        if (!level.shouldLog(minLogLevel)) return

        // Enviar a todos los datasources habilitados
        dataSources.forEach { dataSource ->
            if (dataSource.isEnabled) {
                try {
                    dataSource.log(level, tag, message, throwable)
                } catch (e: Exception) {
                    // Si un datasource falla, no queremos detener los demás
                    println("Error en datasource ${dataSource::class.simpleName}: ${e.message}")
                }
            }
        }
    }

    /**
     * Métodos de conveniencia para logging manual
     */
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.ERROR, tag, message, throwable)

    fun wtf(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.FATAL, tag, message, throwable)
}
