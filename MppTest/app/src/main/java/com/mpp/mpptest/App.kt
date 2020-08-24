package com.mpp.mpptest

import android.app.Application
import android.content.Context
import com.jetbrains.handson.mpp.mobile.source.initKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            modules(module { single<Context> { this@App } })
        }
    }
}