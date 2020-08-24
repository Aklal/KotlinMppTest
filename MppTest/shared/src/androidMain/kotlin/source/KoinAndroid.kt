package com.jetbrains.handson.mpp.mobile.source

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    //val baseKermit = Kermit(LogcatLogger()).withTag("MppTest")
    val baseKermit = Kermit(CommonLogger()).withTag("MppTest")

    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}