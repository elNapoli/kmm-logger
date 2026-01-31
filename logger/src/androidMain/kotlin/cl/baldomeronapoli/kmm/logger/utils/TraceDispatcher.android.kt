package cl.baldomeronapoli.kmm.logger.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * En Android usamos Dispatchers.IO para operaciones de logging
 * que pueden involucrar I/O (escribir a archivos, network, etc.)
 */
actual fun traceDispatcher(): CoroutineDispatcher = Dispatchers.IO