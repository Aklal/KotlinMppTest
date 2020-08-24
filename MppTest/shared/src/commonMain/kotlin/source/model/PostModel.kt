package com.jetbrains.handson.mpp.mobile.source.model

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
import com.jetbrains.handson.mpp.mobile.source.ktor.PostApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

class PostModel(
    private val errorUpdate: (String) -> Unit
) : BaseModel() {
    private val ktorApi: PostApi by inject()
    private val log: Kermit by inject { parametersOf("BreedModel") }

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    init {
        ensureNeverFrozen()
        scope.launch {
            val localLog = log

        }
    }

    fun getPostsFromNetwork(): Job {
        return scope.launch {
            try {
                val postsListResult = ktorApi.getAllPost()
                log.v { "Post list size: ${postsListResult.size}" }
            } catch (e: Exception) {
                errorUpdate("Unable to download breed list")
            }
        }
    }
}



