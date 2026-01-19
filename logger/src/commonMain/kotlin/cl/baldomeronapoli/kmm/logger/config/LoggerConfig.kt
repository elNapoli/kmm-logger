package cl.baldomeronapoli.kmm.logger.config

/**
 * Configuración para el sistema de logging.
 * La aplicación consumidora debe implementar esta interfaz y proveerla via DI.
 *
 * @example
 * ```kotlin
 * class MyAppLoggerConfig : LoggerConfig {
 *     override val enableNapier = BuildConfig.DEBUG
 *     override val enableCrashlytics = true
 *     override val crashlytics = FirebaseCrashlytics.getInstance()
 *     override val minLogLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
 * }
 * ```
 */
interface LoggerConfig {
    /**
     * Habilitar logging via Napier (console/debug logs).
     * Recomendado: true en debug, false en release.
     */
    val enableNapier: Boolean get() = true

    /**
     * Habilitar logging a Firebase Crashlytics (crash reporting).
     * Requiere que la app haya inicializado Firebase.
     */
    val enableCrashlytics: Boolean get() = false

    /**
     * Instancia de Firebase Crashlytics ya inicializada por la app.
     * Solo se usa si [enableCrashlytics] es true.
     *
     * En Android: FirebaseCrashlytics.getInstance()
     * En iOS: NSNull (placeholder, implementación pendiente)
     */
    val crashlytics: Any? get() = null

    /**
     * Nivel mínimo de logging.
     * Solo se loggearán mensajes con nivel >= a este valor.
     */
    val minLogLevel: LogLevel get() = LogLevel.INFO

    /**
     * Nivel mínimo para enviar a Crashlytics.
     * Típicamente solo errores y fatales van a Crashlytics.
     */
    val crashlyticsMinLevel: LogLevel get() = LogLevel.ERROR
}
