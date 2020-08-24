package com.jetbrains.handson.mpp.mobile.source

import com.jetbrains.handson.mpp.mobile.source.ktor.PostApi
import com.jetbrains.handson.mpp.mobile.source.ktor.PostApiImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(platformModule, coreModule)
}

private val coreModule = module {
    single<PostApi> { PostApiImpl(getWith("PostApiImpl")) }
}

internal inline fun <reified T> Scope.getWith(vararg params:Any?):T{
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module