package com.jetbrains.handson.mpp.mobile.source.ktor

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.jetbrains.handson.mpp.mobile.remoteapi.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom


class PostApiImpl(private val log: Kermit) : PostApi {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(Logging) {
            logger = object : io.ktor.client.features.logging.Logger {
                override fun log(message: String) {
                    log.v("Network") { message }
                }
            }

            level = LogLevel.INFO
        }
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getAllPost(): List<Post> = network {
        log.d { "Fetching Posts from network" }
        client.get<List<Post>> {
            posts("posts")
        }
    }

    private fun HttpRequestBuilder.posts(path: String) {
        url {
            takeFrom("https://jsonplaceholder.typicode.com/")
            encodedPath = path
        }
    }
}

internal expect suspend fun <R> network(block: suspend () -> R): R