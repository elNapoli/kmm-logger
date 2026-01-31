package cl.baldomeronapoli.kmm.logger

import cl.baldomeronapoli.kmm.logger.domain.repository.LoggingRepository
import cl.baldomeronapoli.kmm.logger.utils.generateTag
import cl.baldomeronapoli.kmm.logger.utils.traceDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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

    private val repository: LoggingRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + traceDispatcher())

    /**
     * Verbose - información muy detallada
     */
    fun v(message: String, throwable: Throwable? = null) {
        v(generateTag(), message, throwable)
    }

    fun v(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            repository.v(tag, message, throwable)
        }
    }

    /**
     * Debug - información de depuración
     */
    fun d(message: String, throwable: Throwable? = null) {
        d(generateTag(), message, throwable)
    }

    fun d(tag: String, message: String, throwable: Throwable? = null) {
        scope.launch {
            repository.d(tag, message, throwable)
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
            repository.i(tag, message, throwable)
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
            repository.w(tag, message, throwable)
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
            repository.e(tag, message, throwable)
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
            repository.wtf(tag, message, throwable)
        }
    }

    /**
     * Crash - para excepciones no manejadas.
     * Auto-extrae el tag del throwable y su mensaje.
     */
    fun crash(throwable: Throwable) {
        scope.launch {
            repository.crash(throwable)
        }
    }
}