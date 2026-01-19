package cl.baldomeronapoli.kmm.logger.writer

import cl.baldomeronapoli.kmm.logger.config.LogLevel

/**
 * Writer que envía logs a Firebase Crashlytics.
 * Usa expect/actual para las implementaciones específicas de plataforma.
 *
 * @param crashlyticsInstance Instancia de Firebase Crashlytics proporcionada por la app.
 *                            Android: FirebaseCrashlytics
 *                            iOS: Por implementar
 * @param isEnabled Si este writer está habilitado
 * @param minLevel Nivel mínimo para enviar a Crashlytics
 */
expect class CrashlyticsLogWriter(
    crashlyticsInstance: Any?,
    isEnabled: Boolean,
    minLevel: LogLevel
) : LogWriter {
    override val isEnabled: Boolean

    override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    )
}
