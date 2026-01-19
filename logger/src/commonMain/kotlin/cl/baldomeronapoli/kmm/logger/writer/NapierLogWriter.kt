package cl.baldomeronapoli.kmm.logger.writer

import cl.baldomeronapoli.kmm.logger.config.LogLevel
import io.github.aakira.napier.Napier

/**
 * Writer que envía logs a Napier.
 * Napier maneja el output a consola/debug según la plataforma.
 */
class NapierLogWriter(
    override val isEnabled: Boolean
) : LogWriter {

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
