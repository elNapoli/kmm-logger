package cl.baldomeronapoli.kmm.logger.writer

import cl.baldomeronapoli.kmm.logger.config.LogLevel

/**
 * Interface para writers de logs.
 * Cada writer es responsable de enviar logs a un destino específico
 * (console, file, crashlytics, etc.)
 */
interface LogWriter {
    /**
     * Verifica si este writer está habilitado.
     */
    val isEnabled: Boolean

    /**
     * Escribe un mensaje de log.
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
    )
}
