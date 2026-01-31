package cl.baldomeronapoli.kmm.logger.utils

/**
 * Genera un tag automático basado en el stack trace.
 * Formato: "NombreArchivo.kt:123"
 */
internal fun generateTag(): String {
    return try {
        val stackTrace = Throwable().stackTraceToString()
        val lines = stackTrace.lines()

        // Buscar la primera línea que NO sea de Trace o LoggingRepository
        val callerLine = lines.firstOrNull { line ->
            !line.contains("Trace.") &&
            !line.contains("LoggingRepository") &&
            !line.contains("StackTraceUtils") &&
            line.contains(".kt:")
        }

        if (callerLine != null) {
            // Extraer el nombre del archivo y línea
            // Formato común: "at package.ClassName.method(FileName.kt:123)"
            val regex = """([A-Za-z0-9_]+\.kt):(\d+)""".toRegex()
            val match = regex.find(callerLine)

            if (match != null) {
                val fileName = match.groupValues[1]
                val lineNumber = match.groupValues[2]
                return "$fileName:$lineNumber"
            }
        }

        "UnknownSource"
    } catch (e: Exception) {
        "UnknownSource"
    }
}