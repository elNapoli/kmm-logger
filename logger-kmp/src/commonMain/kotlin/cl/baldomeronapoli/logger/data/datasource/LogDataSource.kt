package cl.baldomeronapoli.logger.data.datasource

import cl.baldomeronapoli.logger.domain.model.LogLevel

/**
 * DataSource para enviar logs a un destino específico.
 * Cada implementación representa un canal de logging diferente
 * (consola, crashlytics, archivo, analytics, etc.)
 */
interface LogDataSource {
    /**
     * Verifica si este datasource está habilitado.
     */
    val isEnabled: Boolean

    /**
     * Escribe un mensaje de log al destino específico.
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
