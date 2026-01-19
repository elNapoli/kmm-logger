package cl.baldomeronapoli.kmm.logger

import cl.baldomeronapoli.kmm.base.feature.Feature
import cl.baldomeronapoli.kmm.logger.di.loggerModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.module.Module

/**
 * Feature de logging para integración con napoli-kmm-base.
 *
 * Este feature:
 * - Inicializa Napier con antilog apropiado
 * - Registra el módulo de DI con LoggingRepository
 * - Se integra con el sistema de Features de base
 *
 * **Prioridad**: 10 (alta prioridad - debe cargarse temprano)
 *
 * @param enableDebugAntilog Si true, usa DebugAntilog (verbose). Si false, usa antilog por defecto (menos verbose)
 */
class LoggerFeature(
    private val enableDebugAntilog: Boolean = true
) : Feature {

    override val featureName: String = "logger"

    /**
     * Alta prioridad para que el logging esté disponible desde el inicio
     */
    override val priority: Int = 10

    override fun provideDependencies(): List<Module> {
        return listOf(loggerModule)
    }

    override fun initialize() {
        // Configurar Napier
        if (enableDebugAntilog) {
            Napier.base(DebugAntilog())
        }

        Napier.i("LoggerFeature initialized", tag = "LoggerFeature")
    }

    override fun dispose() {
        Napier.i("LoggerFeature disposed", tag = "LoggerFeature")
        // Napier no requiere cleanup explícito
    }
}
