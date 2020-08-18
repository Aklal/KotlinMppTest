package com.mpp.mpptest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.touchlab.kermit.Kermit
import android.widget.TextView
import co.touchlab.kermit.LogcatLogger
import com.jetbrains.handson.mpp.mobile.createApplicationScreenMessage
import com.jetbrains.handson.mpp.mobile.source.model.PostModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity(), KoinComponent {

    private lateinit var model: PostModel
    private val log: Kermit by inject { parametersOf("MainActivity") }

    override fun onCreate(savedInstanceState: Bundle?) {
        val kermit = Kermit(LogcatLogger())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage() + "++"

        model = PostModel(errorUpdate = { errorMessage ->
            log.e { "Error displayed: $errorMessage" }
            //Snackbar.make(breed_list, errorMessage, Snackbar.LENGTH_SHORT).show()
        })
        
        model.getPostsFromNetwork()
    }
}