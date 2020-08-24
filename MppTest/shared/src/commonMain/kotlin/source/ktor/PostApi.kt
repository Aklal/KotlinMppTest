package com.jetbrains.handson.mpp.mobile.source.ktor

import com.jetbrains.handson.mpp.mobile.remoteapi.model.Post

interface PostApi {
    suspend fun getAllPost(): List<Post>
}