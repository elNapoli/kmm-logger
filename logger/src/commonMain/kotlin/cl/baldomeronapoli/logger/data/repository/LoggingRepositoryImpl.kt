package cl.baldomeronapoli.logger.data.repository

import cl.baldomeronapoli.logger.data.datasource.LogDataSource
import cl.baldomeronapoli.logger.domain.model.LogLevel
import cl.baldomeronapoli.logger.domain.repository.LoggingRepository

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
     * Loggea un crash/excepción no manejada.
     * Auto-extrae tag y mensaje del throwable.
     * Usado automáticamente por ExceptionHandler en UseCases o manualmente con Trace.crash()
     */
    override suspend fun crash(throwable: Throwable) {
        log(
            level = LogLevel.ERROR,
            tag = throwable::class.simpleName ?: "Crash",
            message = throwable.message ?: "Crash without message",
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
     * Implementación de métodos de la interfaz LoggingRepository
     */
    override suspend fun v(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.VERBOSE, tag, message, throwable)
    }

    override suspend fun d(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.DEBUG, tag, message, throwable)
    }

    override suspend fun i(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.INFO, tag, message, throwable)
    }

    override suspend fun w(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.WARN, tag, message, throwable)
    }

    override suspend fun e(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.ERROR, tag, message, throwable)
    }

    override suspend fun wtf(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.FATAL, tag, message, throwable)
    }
}
