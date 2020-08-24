package com.jetbrains.handson.mpp.mobile.remoteapi.api

import com.jetbrains.handson.mpp.mobile.remoteapi.model.Post
import com.jetbrains.handson.mpp.mobile.remoteapi.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


class PostApiClient constructor(
    httpClientEngine: HttpClientEngine? = null
) {

    companion object {
        const val BASE_ENDPOINT = "http://jsonplaceholder.typicode.com"
        const val ENDPOINT = "$BASE_ENDPOINT/posts"
        val jsonInst = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    }

    private val client: HttpClient = HttpClient(httpClientEngine!!) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun getPosts(): Either<RemoteApiError, List<Post>> = try {

        val postsJson = client.get<String>(ENDPOINT)

        val posts = jsonInst.parse(Post.serializer().list, postsJson)
        
        Either.Right(posts)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun getPostById(id: String): Either<RemoteApiError, Post> = try {
        val post = client.get<Post>("$ENDPOINT/$id")

        Either.Right(post)
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