package cl.baldomeronapoli.kmm.logger.data.datasource

import cl.baldomeronapoli.kmm.logger.domain.model.LogLevel
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * DataSource que envía logs a Napier.
 * Napier maneja el output a consola/debug según la plataforma.
 *
 * IMPORTANTE: Inicializa Napier automáticamente para encapsular
 * la dependencia. Los usuarios de la librería NO necesitan conocer Napier.
 */
class NapoliLogDataSource(
    override val isEnabled: Boolean
) : LogDataSource {

    companion object Companion {
        private var isNapoliInitialized = false

        /**
         * Inicializa Napier automáticamente si aún no lo está.
         * Esto encapsula Napier como detalle de implementación.
         */
        private fun ensureNapierInitialized() {
            if (!isNapoliInitialized) {
                try {
                    Napier.base(DebugAntilog())
                    isNapoliInitialized = true
                } catch (e: Exception) {
                    // Napier ya está inicializado externamente, o error
                    // En cualquier caso, marcamos como inicializado
                    isNapoliInitialized = true
                }
            }
        }
    }

    init {
        // Asegurar que Napier esté inicializado al crear este datasource
        ensureNapierInitialized()
    }

    override fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        if (!isEnabled) return

        val formattedMessage = "[$tag] $message"

        when (level) {
            LogLevel.DEBUG -> Napier.d(formattedMessage, throwable, tag)
            LogLevel.INFO -> Napier.i(formattedMessage, throwable, tag)
            LogLevel.WARN -> Napier.w(formattedMessage, throwable, tag)
            LogLevel.ERROR -> Napier.e(formattedMessage, throwable, tag)
            LogLevel.FATAL -> Napier.e("FATAL: $formattedMessage", throwable, tag)
            LogLevel.VERBOSE -> Napier.v(formattedMessage, throwable, tag)
        }
    }
}
