# napoli-kmm-logger

Librería de logging modular y escalable para Kotlin Multiplatform (Android & iOS) con soporte para múltiples destinos de logs.

## Características

- **Multi-destino**: Soporte para Napier (consola) y Firebase Crashlytics
- **Configurable**: Habilita/deshabilita destinos individualmente
- **Niveles de log**: DEBUG, INFO, WARN, ERROR, FATAL con filtrado configurable
- **Auto-logging**: Captura automática de excepciones desde `ExceptionHandler` (UseCases)
- **Manual logging**: Métodos de conveniencia (`d()`, `i()`, `w()`, `e()`, `wtf()`)
- **Integración con base**: Implementa `LoggingRepository` de `napoli-kmm-base`
- **Koin DI**: Inyección de dependencias lista para usar
- **Feature-based**: Integración con `FeatureManager` de base

## Requisitos

- Kotlin Multiplatform 2.0+
- [napoli-kmm-base](https://github.com/elNapoli/napoli-kmm-base) - Contiene la interfaz `LoggingRepository` y exporta Napier como API
- Koin 4.0+
- Firebase Crashlytics (opcional, solo si quieres usar Crashlytics)

## Instalación

### 1. Agregar el repositorio

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal() // Si publicas localmente
        maven {
            url = uri("https://github.com/elNapoli/kmm-logger")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### 2. Agregar la dependencia

```kotlin
// gradle/libs.versions.toml
[versions]
napoli-kmm-logger = "1.0.0" # Usa la última versión

[libraries]
napoli-kmm-logger = { module = "cl.baldomeronapoli.kmm:logger", version.ref = "napoli-kmm-logger" }
```

```kotlin
// composeApp/build.gradle.kts (o tu módulo principal)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.napoli.kmm.base)    // Requerido
            implementation(libs.napoli.kmm.logger)
        }

        androidMain.dependencies {
            // Solo si vas a usar Crashlytics en Android
            implementation(libs.firebase.crashlytics)
        }
    }
}
```

### 3. Inicializar Firebase (Opcional - solo si usas Crashlytics)

**La librería NO inicializa Firebase. Tu aplicación debe hacerlo.**

#### Android

```kotlin
// En tu Application class
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
    }
}
```

```kotlin
// build.gradle.kts (app level)
plugins {
    id("com.google.gms.google-services") version "4.4.0"
    id("com.google.firebase.crashlytics") version "2.9.9"
}
```

#### iOS

```swift
// En tu AppDelegate o @main App
import FirebaseCore

@main
struct MyApp: App {
    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## Configuración

### 1. Implementar LoggerConfig

Crea tu propia configuración implementando `LoggerConfig`:

```kotlin
// En tu app
import cl.baldomeronapoli.kmm.logger.config.LoggerConfig
import cl.baldomeronapoli.kmm.logger.config.LogLevel
import com.google.firebase.crashlytics.FirebaseCrashlytics // Solo Android

class MyAppLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = true

    override val enableCrashlytics: Boolean = true

    override val crashlytics: Any? = try {
        FirebaseCrashlytics.getInstance() // Android
    } catch (e: Exception) {
        null // iOS o Firebase no inicializado
    }

    override val minLogLevel: LogLevel = if (BuildConfig.DEBUG) {
        LogLevel.DEBUG
    } else {
        LogLevel.INFO
    }

    override val crashlyticsMinLevel: LogLevel = LogLevel.ERROR
}
```

### 2. Configurar Koin

#### Opción A: Usando FeatureManager (Recomendado)

```kotlin
import cl.baldomeronapoli.kmm.base.di.initKoin
import cl.baldomeronapoli.kmm.logger.LoggerFeature
import cl.baldomeronapoli.kmm.logger.config.LoggerConfig

fun initializeApp() {
    initKoin(
        appModule = module {
            // Proveer tu configuración
            single<LoggerConfig> { MyAppLoggerConfig() }
        },
        features = listOf(
            LoggerFeature(enableDebugAntilog = true),
            // ... otros features
        )
    )
}
```

#### Opción B: Koin tradicional

```kotlin
import cl.baldomeronapoli.kmm.logger.di.loggerModule
import cl.baldomeronapoli.kmm.logger.config.LoggerConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun initializeApp() {
    // Inicializar Napier manualmente
    Napier.base(DebugAntilog())

    startKoin {
        modules(
            module {
                single<LoggerConfig> { MyAppLoggerConfig() }
            },
            loggerModule
        )
    }
}
```

## Uso

### Auto-logging (Captura automática de excepciones)

El logger se integra automáticamente con `ExceptionHandler` de `napoli-kmm-base`. Todas las excepciones no manejadas en tus UseCases serán enviadas a `LoggingRepository.logException()`.

```kotlin
class MyUseCase(
    private val repository: MyRepository
) : UseCase<MyInput, MyOutput> {

    override suspend fun execute(input: MyInput): MyOutput {
        // Si lanza excepción, ExceptionHandler la captura automáticamente
        // y llama a LoggingRepository.logException()
        return repository.getData(input)
    }
}
```

### Manual logging

Inyecta `LoggingRepository` donde lo necesites:

```kotlin
class MyViewModel(
    private val loggingRepository: LoggingRepository
) : ViewModel() {

    fun loadData() {
        // Usando LoggingRepositoryImpl con métodos de conveniencia
        val logger = loggingRepository as? LoggingRepositoryImpl

        logger?.d("MyViewModel", "Loading data...")

        try {
            // Tu lógica
            logger?.i("MyViewModel", "Data loaded successfully")
        } catch (e: Exception) {
            logger?.e("MyViewModel", "Failed to load data", e)
        }
    }

    // O usando el método estándar
    suspend fun logError(error: Throwable) {
        loggingRepository.logException(error)
    }
}
```

### Métodos disponibles

```kotlin
val logger = loggingRepository as LoggingRepositoryImpl

// Niveles de log
logger.d(tag = "TAG", message = "Debug message")
logger.i(tag = "TAG", message = "Info message")
logger.w(tag = "TAG", message = "Warning message")
logger.e(tag = "TAG", message = "Error message", throwable = exception)
logger.wtf(tag = "TAG", message = "Fatal error", throwable = exception)

// Genérico
logger.log(
    level = LogLevel.ERROR,
    tag = "TAG",
    message = "Custom message",
    throwable = exception
)

// Desde la interfaz (suspendible)
loggingRepository.logException(throwable)
```

## Configuración avanzada

### Niveles de log personalizados

```kotlin
class ProductionLoggerConfig : LoggerConfig {
    // Solo logs de ERROR o superior en producción
    override val minLogLevel: LogLevel = LogLevel.ERROR

    // Crashlytics solo recibe FATAL
    override val crashlyticsMinLevel: LogLevel = LogLevel.FATAL

    // Deshabilitar Napier en producción
    override val enableNapier: Boolean = false

    // Solo Crashlytics en producción
    override val enableCrashlytics: Boolean = true
}
```

### Solo Napier (sin Crashlytics)

```kotlin
class SimpleLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = true
    override val enableCrashlytics: Boolean = false
    override val crashlytics: Any? = null
    override val minLogLevel: LogLevel = LogLevel.DEBUG
}
```

### Deshabilitar todo logging

```kotlin
class NoLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = false
    override val enableCrashlytics: Boolean = false
    override val minLogLevel: LogLevel = LogLevel.FATAL
}
```

## Arquitectura

### Writer System

La librería usa un sistema de "writers" pluggables:

- **NapierLogWriter**: Escribe a consola usando Napier (de base)
- **CrashlyticsLogWriter**: Envía logs a Firebase Crashlytics
  - Android: Totalmente implementado con Firebase Crashlytics
  - iOS: TODO - Por implementar

### Flujo de datos

```
App → LoggingRepository → LoggingRepositoryImpl → [Writers]
                                                   ├─ NapierLogWriter → Consola
                                                   └─ CrashlyticsLogWriter → Firebase
```

### Filtrado de logs

1. **Nivel mínimo global** (`minLogLevel`): Filtra antes de enviar a writers
2. **Nivel mínimo por writer** (ej. `crashlyticsMinLevel`): Filtra dentro del writer específico
3. **Enable/Disable**: Cada writer puede estar habilitado/deshabilitado

## Troubleshooting

### "CrashlyticsLogWriter está habilitado pero no se proporcionó una instancia válida"

**Causa**: Intentas habilitar Crashlytics pero no pasaste `FirebaseCrashlytics.getInstance()`.

**Solución**:
1. Asegúrate de inicializar Firebase en tu app antes de inicializar Koin
2. Pasa la instancia correcta en tu `LoggerConfig`:

```kotlin
override val crashlytics: Any? = FirebaseCrashlytics.getInstance()
```

### Los logs no aparecen en Crashlytics

**Solución**:
1. Verifica que Firebase esté correctamente inicializado
2. Verifica que `enableCrashlytics = true` en tu config
3. Verifica que el nivel del log sea >= `crashlyticsMinLevel`
4. Los logs pueden tardar algunos minutos en aparecer en la consola de Firebase

### Logs duplicados

**Causa**: Napier y Crashlytics están ambos habilitados (comportamiento esperado).

**Solución**: Si solo quieres un destino, deshabilita el otro en tu `LoggerConfig`.

## Licencia

MIT License

## Contribuir

Las contribuciones son bienvenidas. Por favor, abre un issue o PR en GitHub.

## Roadmap

- [ ] Implementar CrashlyticsLogWriter para iOS
- [ ] Agregar más writers (ej. File, Sentry, DataDog)
- [ ] Soporte para filtros personalizados
- [ ] Modo batch para optimizar envío a Crashlytics