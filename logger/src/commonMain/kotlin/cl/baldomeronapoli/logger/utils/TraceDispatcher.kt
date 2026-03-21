package cl.baldomeronapoli.logger.utils

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provee el dispatcher adecuado para cada plataforma.
 */
expect fun traceDispatcher(): CoroutineDispatcher