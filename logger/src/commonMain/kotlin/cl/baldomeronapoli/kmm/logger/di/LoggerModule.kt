package cl.baldomeronapoli.kmm.logger.di

import cl.baldomeronapoli.kmm.base.domain.repository.LoggingRepository
import cl.baldomeronapoli.kmm.logger.domain.model.LoggerConfig
import cl.baldomeronapoli.kmm.logger.data.datasource.CrashlyticsLogDataSource
import cl.baldomeronapoli.kmm.logger.data.datasource.LogDataSource
import cl.baldomeronapoli.kmm.logger.data.datasource.NapierLogDataSource
import cl.baldomeronapoli.kmm.logger.data.repository.LoggingRepositoryImpl
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
            NapierLogDataSource(isEnabled = config.enableNapier)
        }

        single<LoggingRepository> {
            val config = get<LoggerConfig>()
            val napierDataSource = get<LogDataSource>()

            // Lista de datasources - solo agregamos los que están habilitados
            val dataSources = buildList {
                add(napierDataSource)

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
