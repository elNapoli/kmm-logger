package cl.baldomeronapoli.kmm.logger

import cl.baldomeronapoli.kmm.base.domain.repositories.LoggingRepository
import cl.baldomeronapoli.kmm.logger.config.LogLevel
import cl.baldomeronapoli.kmm.logger.writer.LogWriter

/**
 * Implementación del repositorio de logging.
 * Coordina múltiples writers (Napier, Crashlytics, etc.) para enviar logs a diferentes destinos.
 *
 * @param writers Lista de writers configurados
 * @param minLogLevel Nivel mínimo global de logging
 */
class LoggingRepositoryImpl(
    private val writers: List<LogWriter>,
    private val minLogLevel: LogLevel
) : LoggingRepository {

    /**
     * Loggea una excepción a todos los writers habilitados.
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

        // Enviar a todos los writers habilitados
        writers.forEach { writer ->
            if (writer.isEnabled) {
                try {
                    writer.log(level, tag, message, throwable)
                } catch (e: Exception) {
                    // Si un writer falla, no queremos detener los demás
                    println("Error en writer ${writer::class.simpleName}: ${e.message}")
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
