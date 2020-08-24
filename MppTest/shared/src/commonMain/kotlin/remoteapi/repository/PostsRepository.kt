package com.jetbrains.handson.mpp.mobile.remoteapi.repository

import com.jetbrains.handson.mpp.mobile.remoteapi.api.PostApiClient
import com.jetbrains.handson.mpp.mobile.remoteapi.model.Post
import com.jetbrains.handson.mpp.mobile.remoteapi.utils.Either


interface PostsRepository {
    suspend fun getPostsList(): List<Post>
    suspend fun getPostById(id: Int): Post
}

class PostsRepositoryImpl(private val api: PostApiClient) : PostsRepository {
    override suspend fun getPostsList(): List<Post> {

        val res = when (val result = api.getPosts()) {
            is Either.Right<List<Post>> -> result.value
            is Either.Left -> emptyList()
        }

        return res
    }


    override suspend fun getPostById(id: Int): Post =
        when (val result = api.getPostById(id.toString())) {
            is Either.Right<Post> -> result.value
            is Either.Left ->  throw IllegalArgumentException()
        }
}