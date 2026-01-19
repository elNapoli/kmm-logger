package cl.baldomeronapoli.kmm.logger.di

import cl.baldomeronapoli.kmm.base.domain.repositories.LoggingRepository
import cl.baldomeronapoli.kmm.logger.LoggingRepositoryImpl
import cl.baldomeronapoli.kmm.logger.config.LoggerConfig
import cl.baldomeronapoli.kmm.logger.writer.CrashlyticsLogWriter
import cl.baldomeronapoli.kmm.logger.writer.LogWriter
import cl.baldomeronapoli.kmm.logger.writer.NapierLogWriter
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Módulo de Koin para el sistema de logging.
 *
 * **Requisito**: La aplicación consumidora debe proporcionar una implementación de [LoggerConfig]
 * en su configuración de Koin antes de incluir este módulo.
 *
 * @example
 * ```kotlin
 * // En la app
 * startKoin {
 *     modules(
 *         module {
 *             single<LoggerConfig> { MyAppLoggerConfig() }
 *         },
 *         loggerModule  // Este módulo
 *     )
 * }
 * ```
 */
val loggerModule = module {
    // LoggerConfig debe ser provisto por la app consumidora
    // Este módulo lo consume para configurar los writers

    // Writers
    single<LogWriter> {
        val config = get<LoggerConfig>()
        NapierLogWriter(isEnabled = config.enableNapier)
    }

    single<CrashlyticsLogWriter> {
        val config = get<LoggerConfig>()
        CrashlyticsLogWriter(
            crashlyticsInstance = config.crashlytics,
            isEnabled = config.enableCrashlytics,
            minLevel = config.crashlyticsMinLevel
        )
    }

    // LoggingRepository - implementación principal
    single<LoggingRepository> {
        val config = get<LoggerConfig>()
        val napierWriter = get<LogWriter>()
        val crashlyticsWriter = get<CrashlyticsLogWriter>()

        LoggingRepositoryImpl(
            writers = listOf(napierWriter, crashlyticsWriter),
            minLogLevel = config.minLogLevel
        )
    }
}
