package com.jetbrains.handson.mpp.mobile.remoteapi.api


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

    suspend fun getEvents(): Either<RemoteApiError, List<Event>> = try {
        val tasksJson = client.get<String>("$BASE_ENDPOINT/posts")

        val tasks = jsonInst.parse(Event.serializer().list, tasksJson)

        Either.Right(tasks)
    } catch (e: Exception) {
        handleError(e)
    }

    suspend fun getEventById(id: String): Either<RemoteApiError, Event> = try {
        val task = client.get<Event>("$BASE_ENDPOINT/posts/$id")

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