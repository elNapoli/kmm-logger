package cl.baldomeronapoli.logger.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * En iOS usamos Dispatchers.Default para operaciones de logging.
 */
actual fun traceDispatcher(): CoroutineDispatcher = Dispatchers.Default