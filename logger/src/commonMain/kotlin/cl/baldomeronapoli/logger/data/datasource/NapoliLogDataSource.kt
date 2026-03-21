package cl.baldomeronapoli.logger.data.datasource

import cl.baldomeronapoli.logger.domain.model.LogLevel
import cl.baldomeronapoli.logger.utils.NativeLogger

/**
 * DataSource que envía logs al logger nativo de cada plataforma.
 * - Android: usa android.util.Log
 * - iOS: usa NSLog
 *
 * Sin dependencias externas - logging puro nativo.
 */
class NapoliLogDataSource(
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

        NativeLogger.log(level, tag, formattedMessage, throwable)
    }
}
