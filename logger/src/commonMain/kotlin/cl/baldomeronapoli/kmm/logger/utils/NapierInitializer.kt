package cl.baldomeronapoli.kmm.logger.utils

import io.github.aakira.napier.Antilog

/**
 * Función expect para crear el Antilog apropiado según la plataforma.
 * - Android/JVM: DebugAntilog (logcat/console)
 * - iOS: NSLog-based antilog
 */
expect fun createNapierAntilog(): Antilog
