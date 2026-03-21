package cl.baldomeronapoli.logger.di

import cl.baldomeronapoli.logger.data.datasource.CrashlyticsLogDataSource
import cl.baldomeronapoli.logger.data.datasource.LogDataSource
import cl.baldomeronapoli.logger.data.datasource.NapoliLogDataSource
import cl.baldomeronapoli.logger.data.repository.LoggingRepositoryImpl
import cl.baldomeronapoli.logger.domain.model.LoggerConfig
import cl.baldomeronapoli.logger.domain.repository.LoggingRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Módulo de Koin para el sistema de logging siguiendo Clean Architecture.
 *
 * **Requisito**: La aplicación consumidora debe proporcionar una implementación de [LoggerConfig]
 * en su configuración de Koin antes de incluir este módulo.
 *
 * @example
 * ```kotlin
 * // En la app
 * startKoin {
 *     modules(
 *         LoggerModule.getModules() + module {
 *             single<LoggerConfig> { MyAppLoggerConfig() }
 *         }
 *     )
 * }
 * ```
 */
object LoggerModule {

    fun getModules(): List<Module> {
        return listOf(
            platformModule(),
            commonModule()
        )
    }

    private fun commonModule() = module {
        single<LogDataSource> {
            val config = get<LoggerConfig>()
            NapoliLogDataSource(isEnabled = config.enableNapoliLogger)
        }

        single<LoggingRepository> {
            val config = get<LoggerConfig>()
            val napoliDataSource = get<LogDataSource>()

            // Lista de datasources - solo agregamos los que están habilitados
            val dataSources = buildList {
                add(napoliDataSource)

                // Solo crear CrashlyticsLogDataSource si está habilitado
                if (config.enableCrashlytics) {
                    add(
                        CrashlyticsLogDataSource(
                            crashlyticsInstance = config.crashlytics,
                            isEnabled = true,
                            minLevel = config.crashlyticsMinLevel
                        )
                    )
                }
            }

            LoggingRepositoryImpl(
                dataSources = dataSources,
                minLogLevel = config.minLogLevel
            )
        }
    }
}
