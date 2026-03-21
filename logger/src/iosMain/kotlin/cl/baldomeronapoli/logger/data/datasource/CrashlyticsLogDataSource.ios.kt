package cl.baldomeronapoli.logger.data.datasource

import cl.baldomeronapoli.logger.domain.model.LogLevel

/**
 * Implementación iOS del datasource de Crashlytics.
 * TODO: Implementar integración con Firebase Crashlytics iOS SDK.
 */
actual class CrashlyticsLogDataSource actual constructor(
    crashlyticsInstance: Any?,
    isEnabled: Boolean,
    private val minLevel: LogLevel
) : LogDataSource {

    actual override val isEnabled: Boolean = isEnabled

    init {
        if (isEnabled) {
            println("⚠️ CrashlyticsLogDataSource iOS: Implementación pendiente. Los logs no se enviarán a Crashlytics.")
        }
    }

    actual override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        if (!isEnabled) return
        if (!level.shouldLog(minLevel)) return

        // TODO: Implementar cuando se agregue Firebase iOS SDK
        // Por ahora, solo log a consola
        println("📱 iOS Crashlytics [${level.name}][$tag]: $message")
        throwable?.let {
            println("   Exception: ${it.message}")
            it.printStackTrace()
        }
    }
}
