package cl.baldomeronapoli.logger.domain.model

/**
 * Niveles de severidad para logging.
 * Ordenados de menor a mayor severidad.
 */
enum class LogLevel(val priority: Int) {
    /**
     * Información muy detallada, más granular que DEBUG.
     * Solo para debugging profundo en desarrollo.
     */
    VERBOSE(0),

    /**
     * Información detallada para debugging.
     * Solo debería usarse en desarrollo.
     */
    DEBUG(1),

    /**
     * Información general sobre el flujo de la aplicación.
     */
    INFO(2),

    /**
     * Advertencias que no impiden el funcionamiento normal,
     * pero podrían indicar problemas potenciales.
     */
    WARN(3),

    /**
     * Errores que afectan funcionalidad pero la app puede continuar.
     */
    ERROR(4),

    /**
     * Errores críticos/fatales que pueden causar crash de la app.
     */
    FATAL(5);

    /**
     * Verifica si este nivel debería ser loggeado dado un nivel mínimo.
     */
    fun shouldLog(minLevel: LogLevel): Boolean {
        return this.priority >= minLevel.priority
    }
}