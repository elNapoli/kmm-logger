package cl.baldomeronapoli.kmm.logger.utils

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog

/**
 * Implementación Android del Antilog.
 * Usa DebugAntilog que envía logs a Logcat.
 */
actual fun createNapierAntilog(): Antilog = DebugAntilog()