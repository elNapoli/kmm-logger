package cl.baldomeronapoli.kmm.logger.data.datasource

import cl.baldomeronapoli.kmm.logger.domain.model.LogLevel
import io.github.aakira.napier.Napier

/**
 * DataSource que envía logs a Napier.
 * Napier maneja el output a consola/debug según la plataforma.
 */
class NapierLogDataSource(
    override val isEnabled: Boolean
) : LogDataSource {

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
        }
    }
}
