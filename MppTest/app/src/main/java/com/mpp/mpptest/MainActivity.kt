package com.mpp.mpptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jetbrains.handson.mpp.mobile.createApplicationScreenMessage
import com.jetbrains.handson.mpp.mobile.remoteapi.api.PostApiClient
import com.jetbrains.handson.mpp.mobile.remoteapi.repository.PostsRepositoryImpl
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.*
import okhttp3.logging.HttpLoggingInterceptor


class MainActivity : AppCompatActivity() {

    val defaultDispatcher = Dispatchers.Default

    val coroutineErrorHandler = CoroutineExceptionHandler { context, error ->
        println("Problems with Coroutine: ${error}") // we just print the error here
    }

    val emptyParentJob = Job()

    val combinedContext = defaultDispatcher + emptyParentJob


    val mainScope = MainScope()

    val httpClientEngine: HttpClientEngine by lazy {
        OkHttp.create {
            val networkInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addNetworkInterceptor(networkInterceptor)
        }
    }

    private val api = PostApiClient(httpClientEngine)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage() + "..."

        val postRepository: PostsRepositoryImpl by lazy {
            PostsRepositoryImpl(api)
        }

        val job = mainScope.launch { // launch a new coroutine and keep a reference to its Job
            delay(1000L)
            println("World!")

            val ev= postRepository.getPostById(9)

            withContext(Dispatchers.Main){
                Log.d("MAINACTIVITY", "~ Post.title: ${ev.title}")
            }
        }
    }
}