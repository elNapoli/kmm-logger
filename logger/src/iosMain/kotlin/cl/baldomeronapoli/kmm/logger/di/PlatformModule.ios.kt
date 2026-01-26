package cl.baldomeronapoli.kmm.logger.di

import cl.baldomeronapoli.kmm.logger.data.datasource.device.UserAgentDataSource
import cl.baldomeronapoli.kmm.logger.data.datasource.device.UserAgentDataSourceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    singleOf(::UserAgentDataSourceImpl) { bind<UserAgentDataSource>() }
}
