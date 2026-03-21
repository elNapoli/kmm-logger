package cl.baldomeronapoli.logger.data.datasource

import cl.baldomeronapoli.logger.domain.model.LogLevel
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Implementación Android del datasource de Crashlytics.
 */
actual class CrashlyticsLogDataSource actual constructor(
    crashlyticsInstance: Any?,
    isEnabled: Boolean,
    private val minLevel: LogLevel
) : LogDataSource {

    actual override val isEnabled: Boolean = isEnabled
    private val crashlytics: FirebaseCrashlytics? = crashlyticsInstance as? FirebaseCrashlytics

    init {
        if (isEnabled && crashlytics == null) {
            throw IllegalStateException(
                "CrashlyticsLogDataSource está habilitado pero no se proporcionó una instancia válida de FirebaseCrashlytics. " +
                        "Asegúrate de inicializar Firebase en tu app y pasar FirebaseCrashlytics.getInstance() en LoggerConfig."
            )
        }
    }

    actual override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        if (!isEnabled || crashlytics == null) return
        if (!level.shouldLog(minLevel)) return

        try {
            // Log custom key-value para contexto
            crashlytics.setCustomKey("log_level", level.name)
            crashlytics.setCustomKey("log_tag", tag)

            // Log el mensaje
            crashlytics.log("[$tag] ${level.name}: $message")

            // Si hay throwable, registrarlo como excepción
            throwable?.let {
                crashlytics.recordException(it)
            }
        } catch (e: Exception) {
            // Si falla Crashlytics, no queremos romper la app
            // Simplemente ignoramos el error
            println("Error al enviar log a Crashlytics: ${e.message}")
        }
    }
}
