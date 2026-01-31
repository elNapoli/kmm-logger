# Trace Logger 🔍

**Versión 1.0.0**

Librería de logging simple y poderosa para Kotlin Multiplatform (KMM) con tags automáticos y soporte multi-destino.

```kotlin
// Así de simple
Trace.i("Usuario inició sesión exitosamente")
Trace.e("Error al procesar pago", exception)
Trace.crash(exception)
```

---

## ¿Qué es KMM?

**Kotlin Multiplatform Mobile (KMM)** es una tecnología de JetBrains que permite compartir código entre Android e iOS usando Kotlin. Con KMM puedes escribir la lógica de negocio una sola vez y usarla en ambas plataformas, reduciendo duplicación y manteniendo consistencia.

Esta librería está diseñada para funcionar en **Android** e **iOS** sin cambios en tu código.

---

## ✨ Características

- **🎯 Tags automáticos**: No necesitas especificar el tag, se genera automáticamente como `Archivo.kt:123`
- **📱 Multiplataforma**: Funciona en Android e iOS sin configuración adicional
- **🎚️ Múltiples niveles**: v, d, i, w, e, wtf, crash
- **🔌 Multi-destino**: Logs a consola (Napier) y Firebase Crashlytics
- **⚡ No bloqueante**: Todos los logs se ejecutan en coroutines
- **🎛️ Configurable**: Filtra por nivel mínimo, habilita/deshabilita destinos
- **🧩 Koin ready**: Integración lista con Koin DI

---

## 📋 Requisitos

- **Kotlin**: 2.0+
- **Koin**: 4.0+
- **kotlinx-coroutines**: 1.8+
- **Android**: minSdk 24
- **iOS**: iOS 13+

### Dependencias opcionales
- **Firebase Crashlytics**: Solo si quieres enviar logs a Crashlytics

---

## 📦 Instalación

### 1. Agregar el repositorio

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal() // Si lo publicas localmente

        // O desde GitHub Packages
        maven {
            url = uri("https://maven.pkg.github.com/elNapoli/napoli-kmm-logger")
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
trace-logger = "1.0.0"

[libraries]
trace-logger = { module = "cl.baldomeronapoli.kmm:logger", version.ref = "trace-logger" }
```

```kotlin
// composeApp/build.gradle.kts (o tu módulo shared)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.trace.logger)
        }
    }
}
```

---

## ⚙️ Configuración

### 1. Crear tu LoggerConfig

Crea una clase que implemente `LoggerConfig`:

```kotlin
import cl.baldomeronapoli.kmm.logger.domain.model.LoggerConfig
import cl.baldomeronapoli.kmm.logger.domain.model.LogLevel

class MyAppLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = true
    override val enableCrashlytics: Boolean = false
    override val crashlytics: Any? = null

    override val minLogLevel: LogLevel = LogLevel.DEBUG
    override val crashlyticsMinLevel: LogLevel = LogLevel.ERROR
}
```

### 2. Configurar Koin

Inicializa Koin con el módulo de logger:

```kotlin
import cl.baldomeronapoli.kmm.logger.di.LoggerModule
import cl.baldomeronapoli.kmm.logger.domain.model.LoggerConfig
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            // Tu configuración
            module {
                single<LoggerConfig> { MyAppLoggerConfig() }
            },
            // Módulos del logger
            *LoggerModule.getModules().toTypedArray()
        )
    }
}
```

### 3. Inicializar en tu app

#### Android

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
```

#### iOS

```swift
import shared // Tu módulo KMM

@main
struct MyApp: App {
    init() {
        KoinKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

---

## 🚀 Uso

### Tags automáticos

La forma más simple. El tag se genera automáticamente con el formato `Archivo.kt:línea`:

```kotlin
Trace.v("Información muy detallada")
Trace.d("Debug info")
Trace.i("Usuario inició sesión")
Trace.w("Cache cerca del límite")
Trace.e("Error al cargar datos")
Trace.wtf("Error crítico que nunca debería pasar")
```

**Resultado en logcat:**
```
MainActivity.kt:42 - Usuario inició sesión
PaymentViewModel.kt:156 - Error al cargar datos
```

### Tags manuales

Si prefieres especificar el tag manualmente:

```kotlin
Trace.v("NetworkLayer", "Request headers enviados")
Trace.d("MainActivity", "onCreate llamado")
Trace.i("AuthFlow", "Token renovado exitosamente")
Trace.w("CacheManager", "Cache en 90% de capacidad")
Trace.e("PaymentService", "Tarjeta rechazada")
Trace.wtf("DatabaseManager", "Corrupción detectada en BD")
```

### Con excepciones

Puedes agregar una excepción como tercer parámetro:

```kotlin
try {
    paymentService.process()
} catch (e: Exception) {
    // Tag automático + excepción
    Trace.e("Error procesando pago", e)

    // Tag manual + excepción
    Trace.e("PaymentFlow", "Error procesando pago de \$100", e)
}
```

### Crash automático

Para crashes o excepciones no manejadas, usa `crash()` que auto-extrae toda la información:

```kotlin
try {
    criticalOperation()
} catch (e: Exception) {
    // Auto-extrae: tag, mensaje y stack trace
    Trace.crash(e)
}
```

---

## 📊 Niveles de Log

| Método | Nivel | Uso típico |
|--------|-------|------------|
| `v()` | VERBOSE | Información muy detallada, debugging profundo |
| `d()` | DEBUG | Información de depuración durante desarrollo |
| `i()` | INFO | Información general sobre el flujo de la app |
| `w()` | WARNING | Advertencias que no son errores |
| `e()` | ERROR | Errores que afectan funcionalidad |
| `wtf()` | FATAL | Errores críticos que pueden causar crash |
| `crash()` | ERROR | Crashes/excepciones no manejadas |

---

## 🎛️ Configuración avanzada

### Entorno de desarrollo

```kotlin
class DevLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = true          // Logs en consola
    override val enableCrashlytics: Boolean = false    // No enviar a Crashlytics
    override val minLogLevel: LogLevel = LogLevel.VERBOSE  // Todos los logs
}
```

### Entorno de producción

```kotlin
class ProdLoggerConfig : LoggerConfig {
    override val enableNapier: Boolean = false         // Sin logs en consola
    override val enableCrashlytics: Boolean = true     // Enviar a Crashlytics
    override val minLogLevel: LogLevel = LogLevel.ERROR   // Solo errores
    override val crashlyticsMinLevel: LogLevel = LogLevel.FATAL  // Solo críticos

    override val crashlytics: Any? = FirebaseCrashlytics.getInstance()
}
```

### Configuración dinámica según build type

```kotlin
class SmartLoggerConfig(private val isDebug: Boolean) : LoggerConfig {
    override val enableNapier: Boolean = isDebug
    override val enableCrashlytics: Boolean = !isDebug

    override val minLogLevel: LogLevel = if (isDebug) {
        LogLevel.DEBUG
    } else {
        LogLevel.INFO
    }

    override val crashlytics: Any? = if (!isDebug) {
        FirebaseCrashlytics.getInstance()
    } else {
        null
    }
}
```

---

## 🔥 Firebase Crashlytics (Opcional)

Si quieres enviar logs a Firebase Crashlytics:

### 1. Configurar Firebase

#### Android

```kotlin
// build.gradle.kts (project level)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}

// build.gradle.kts (app level)
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(libs.firebase.crashlytics)
}
```

```kotlin
// Application class
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)  // Antes de initKoin()
        initKoin()
    }
}
```

#### iOS

```swift
// En tu @main App
import FirebaseCore

@main
struct MyApp: App {
    init() {
        FirebaseApp.configure()  // Antes de initKoin()
        KoinKt.initKoin()
    }
}
```

### 2. Habilitar en LoggerConfig

```kotlin
class MyAppLoggerConfig : LoggerConfig {
    override val enableCrashlytics: Boolean = true
    override val crashlytics: Any? = FirebaseCrashlytics.getInstance()
    override val crashlyticsMinLevel: LogLevel = LogLevel.ERROR
}
```

---

## 🏗️ Arquitectura

```
┌─────────────┐
│    Trace    │  ← API pública
└──────┬──────┘
       │
       ▼
┌──────────────────────┐
│ LoggingRepository    │  ← Interfaz
└──────────────────────┘
       │
       ▼
┌──────────────────────┐
│ LoggingRepositoryImpl│  ← Implementación
└──────┬───────────────┘
       │
       ├─────────────────┬─────────────────┐
       ▼                 ▼                 ▼
┌─────────────┐  ┌──────────────┐  ┌──────────────┐
│   Napier    │  │ Crashlytics  │  │  Custom...   │
│ DataSource  │  │  DataSource  │  │  DataSource  │
└─────────────┘  └──────────────┘  └──────────────┘
```

### Flujo de logs

1. **Trace.i()** → Genera tag automático (opcional)
2. **LoggingRepository** → Valida nivel mínimo
3. **DataSources** → Envía a destinos habilitados
   - NapierLogDataSource → Consola
   - CrashlyticsLogDataSource → Firebase

---

## 🐛 Troubleshooting

### Los logs no aparecen

**Verifica:**
1. Que `enableNapier = true` en tu `LoggerConfig`
2. Que el nivel del log sea >= `minLogLevel`
3. Que hayas inicializado Koin correctamente

### Error: Unresolved reference 'Trace'

**Solución:**
```kotlin
import cl.baldomeronapoli.kmm.logger.Trace
```

### Crashlytics no recibe logs

**Verifica:**
1. Firebase inicializado ANTES de Koin
2. `enableCrashlytics = true` en tu config
3. `crashlytics = FirebaseCrashlytics.getInstance()` proporcionado
4. Nivel del log >= `crashlyticsMinLevel`
5. Los logs pueden tardar minutos en aparecer en Firebase Console

### NoClassDefFoundError: FirebaseCrashlytics

**Causa**: Tienes `enableCrashlytics = true` pero Firebase no está en tu classpath.

**Solución**:
```kotlin
override val enableCrashlytics: Boolean = false  // Deshabilitar
// O instalar Firebase correctamente
```

---

## 📝 Ejemplos completos

### Ejemplo básico

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Trace.i("Activity creada")

        try {
            loadData()
        } catch (e: Exception) {
            Trace.e("Error cargando datos", e)
        }
    }
}
```

### En un ViewModel

```kotlin
class UserViewModel : ViewModel() {

    fun login(email: String, password: String) {
        Trace.d("LoginFlow", "Intento de login para: $email")

        viewModelScope.launch {
            try {
                val user = authRepository.login(email, password)
                Trace.i("LoginFlow", "Login exitoso: ${user.id}")
            } catch (e: NetworkException) {
                Trace.w("LoginFlow", "Error de red en login", e)
            } catch (e: Exception) {
                Trace.e("LoginFlow", "Error inesperado en login", e)
            }
        }
    }
}
```

### En un Repository

```kotlin
class PaymentRepository {

    suspend fun processPayment(amount: Double): Result<Payment> {
        Trace.d("PaymentRepo", "Procesando pago: $$amount")

        return try {
            val result = api.processPayment(amount)
            Trace.i("PaymentRepo", "Pago exitoso: ${result.id}")
            Result.success(result)
        } catch (e: Exception) {
            Trace.e("PaymentRepo", "Error procesando pago", e)
            Result.failure(e)
        }
    }
}
```

---

## 🗺️ Roadmap

- [x] Tags automáticos
- [x] Soporte Android
- [x] Soporte iOS
- [x] Firebase Crashlytics (Android)
- [ ] Firebase Crashlytics (iOS)
- [ ] Custom DataSources (File, Sentry, etc.)
- [ ] Log filtering por tag/pattern
- [ ] Performance monitoring

---

## 📄 Licencia

MIT License

---

## 🤝 Contribuir

Las contribuciones son bienvenidas! Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

---

## 📧 Contacto

Baldomero Napoli - [@elNapoli](https://github.com/elNapoli)

Project Link: [https://github.com/elNapoli/napoli-kmm-logger](https://github.com/elNapoli/napoli-kmm-logger)

---

**Hecho con ❤️ usando Kotlin Multiplatform**