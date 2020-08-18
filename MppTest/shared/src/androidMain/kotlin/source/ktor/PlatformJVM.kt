package com.jetbrains.handson.mpp.mobile.source.ktor


internal actual suspend fun <R> network(block: suspend () -> R): R = block()
