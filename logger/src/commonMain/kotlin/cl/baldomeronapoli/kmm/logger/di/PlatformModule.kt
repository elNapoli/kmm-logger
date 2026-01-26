package cl.baldomeronapoli.kmm.logger.di

import org.koin.core.module.Module

/**
 * Platform-specific module for logger.
 * Each platform (Android/iOS) provides its own implementation.
 */
expect fun platformModule(): Module
