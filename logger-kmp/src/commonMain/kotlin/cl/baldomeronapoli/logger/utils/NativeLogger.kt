package cl.baldomeronapoli.logger.utils

import cl.baldomeronapoli.logger.domain.model.LogLevel

/**
 * Logger nativo multiplataforma sin dependencias externas.
 * Usa las APIs nativas de logging de cada plataforma.
 */
expect object NativeLogger {
    /**
     * Loguea un mensaje con el nivel especificado.
     * @param level Nivel del log (DEBUG, INFO, WARN, ERROR, etc.)
     * @param tag Tag/categoría del log
     * @param message Mensaje a loguear
     * @param throwable Excepción opcional
     */
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
}
