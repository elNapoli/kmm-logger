package cl.baldomeronapoli.logger

import cl.baldomeronapoli.logger.domain.model.LogLevel
import cl.baldomeronapoli.logger.domain.repository.LoggingRepository
import cl.baldomeronapoli.logger.utils.NativeLogger
import cl.baldomeronapoli.logger.utils.generateTag
import cl.baldomeronapoli.logger.utils.traceDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * API pública del logger Trace.
 *
 * Uso:
 * ```kotlin
 * // Con tag automático (NombreArchivo.kt:línea)
 * Trace.i("Usuario inició sesión")
 *
 * // Con tag manual
 * Trace.d("NetworkRepo", "Request enviado: $url")
 *
 * // Con excepción
 * Trace.e("Error al procesar pago", exception)
 *
 * // Crash automático (extrae info del throwable)
 * Trace.crash(exception)
 * ```
 *
 * Niveles disponibles:
 * - v: Verbose (información muy detallada)
 * - d: Debug (información de depuración)
 * - i: Info (información general)
 * - w: Warning (advertencias)
 * - e: Error (errores)
 * - wtf: What a Terrible Failure (errores críticos)
 * - crash: Para crashes/excepciones (auto-extrae tag y mensaje)
 */
object Trace : KoinComponent {

    private val scope = CoroutineScope(SupervisorJob() + traceDispatcher())


    // Obtener el repository de forma segura, con fallback a null si falla
    private fun getRepository(): LoggingRepository? {
        return try {
            getKoin().get<LoggingRepository>()
        } catch (e: Exception) {
            NativeLogger.log(LogLevel.ERROR, "Trace", "CRITICAL: Failed to get LoggingRepository from Koin", e)
            null
        }
    }

    /**
     * Verbose - información muy detallada
     */
    fun v(message: String, throwable: Throwable? = null) {
        v(generateTag(), message, throwable)
    }

    fun v(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.v(tag, message, throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.v failed", e)
                    NativeLogger.log(LogLevel.VERBOSE, tag, message, throwable)
                }
            } else {
                NativeLogger.log(LogLevel.VERBOSE, tag, message, throwable)
            }
        }
    }

    /**
     * Debug - información de depuración
     */
    fun d(message: String, throwable: Throwable? = null) {
        d(generateTag(), message, throwable)
    }

    fun d(tag: String, message: String, throwable: Throwable? = null) {
        // En iOS, las coroutines pueden tener delay, así que logueamos inmediatamente también
        // Esto asegura que los logs críticos nunca se pierdan
        NativeLogger.log(LogLevel.DEBUG, tag, message, throwable)

        // También intentamos loguear vía repository (para Crashlytics u otros destinos)
        scope.launch {
            val repo = getRepository()
            repo?.let {
                try {
                    it.d(tag, message, throwable)
                } catch (e: Exception) {
                    // Error al usar repository, pero ya logueamos arriba así que está ok
                }
            }
        }
    }

    /**
     * Info - información general
     */
    fun i(message: String, throwable: Throwable? = null) {
        i(generateTag(), message, throwable)
    }

    fun i(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.i(tag, message, throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.i failed", e)
                    NativeLogger.log(LogLevel.INFO, tag, message, throwable)
                }
            } else {
                NativeLogger.log(LogLevel.INFO, tag, message, throwable)
            }
        }
    }

    /**
     * Warning - advertencias
     */
    fun w(message: String, throwable: Throwable? = null) {
        w(generateTag(), message, throwable)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.w(tag, message, throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.w failed", e)
                    NativeLogger.log(LogLevel.WARN, tag, message, throwable)
                }
            } else {
                NativeLogger.log(LogLevel.WARN, tag, message, throwable)
            }
        }
    }

    /**
     * Error - errores
     */
    fun e(message: String, throwable: Throwable? = null) {
        e(generateTag(), message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.e(tag, message, throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.e failed", e)
                    NativeLogger.log(LogLevel.ERROR, tag, message, throwable)
                }
            } else {
                NativeLogger.log(LogLevel.ERROR, tag, message, throwable)
            }
        }
    }

    /**
     * What a Terrible Failure - errores críticos
     */
    fun wtf(message: String, throwable: Throwable? = null) {
        wtf(generateTag(), message, throwable)
    }

    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.wtf(tag, message, throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.wtf failed", e)
                    NativeLogger.log(LogLevel.FATAL, tag, message, throwable)
                }
            } else {
                NativeLogger.log(LogLevel.FATAL, tag, message, throwable)
            }
        }
    }

    /**
     * Crash - para excepciones no manejadas.
     * Auto-extrae el tag del throwable y su mensaje.
     */
    fun crash(throwable: Throwable) {
        scope.launch {
            val repo = getRepository()
            if (repo != null) {
                try {
                    repo.crash(throwable)
                } catch (e: Exception) {
                    NativeLogger.log(LogLevel.ERROR, "Trace", "Trace.crash failed", e)
                    NativeLogger.log(LogLevel.ERROR, "CRASH", throwable.message ?: "Unknown crash", throwable)
                }
            } else {
                NativeLogger.log(LogLevel.ERROR, "CRASH", throwable.message ?: "Unknown crash", throwable)
            }
        }
    }
}
