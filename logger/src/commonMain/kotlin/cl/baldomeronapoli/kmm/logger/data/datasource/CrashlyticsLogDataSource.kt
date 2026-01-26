package cl.baldomeronapoli.kmm.logger.data.datasource

import cl.baldomeronapoli.kmm.logger.domain.model.LogLevel

/**
 * DataSource que envía logs a Firebase Crashlytics.
 * Usa expect/actual para las implementaciones específicas de plataforma.
 *
 * @param crashlyticsInstance Instancia de Firebase Crashlytics proporcionada por la app.
 *                            Android: FirebaseCrashlytics
 *                            iOS: Por implementar
 * @param isEnabled Si este datasource está habilitado
 * @param minLevel Nivel mínimo para enviar a Crashlytics
 */
expect class CrashlyticsLogDataSource(
    crashlyticsInstance: Any?,
    isEnabled: Boolean,
    minLevel: LogLevel
) : LogDataSource {
    override val isEnabled: Boolean

    override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    )
}
