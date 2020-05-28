package com.jetbrains.handson.mpp.mobile.remoteapi.api

import com.jetbrains.handson.mpp.mobile.remoteapi.model.Post
import com.jetbrains.handson.mpp.mobile.remoteapi.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


class PostApiClient constructor(
    httpClientEngine: HttpClientEngine? = null
) {

    companion object {
        const val BASE_ENDPOINT = "http://jsonplaceholder.typicode.com"
        val jsonInst = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    }

    private val client: HttpClient = HttpClient(httpClientEngine!!) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun getPosts(): Either<RemoteApiError, List<Post>> = try {
        val tasksJson = client.get<String>("$BASE_ENDPOINT/posts")

        val tasks = jsonInst.parse(Post.serializer().list, tasksJson)

        Either.Right(tasks)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun getPostById(id: String): Either<RemoteApiError, Post> = try {
        val task = client.get<Post>("$BASE_ENDPOINT/posts/$id")

        Either.Right(task)
    } catch (e: Exception) {
        handleError(e)
    }

    private fun handleError(exception: Exception): Either<RemoteApiError, Nothing> =
        if (exception is HttpResponse) {
            if (exception.status.value == 404) {
                Either.Left(ItemNotFoundError)
            } else {
                Either.Left(UnknownError(exception.status.value))
            }
        } else {
            Either.Left(NetworkError)
        }
}