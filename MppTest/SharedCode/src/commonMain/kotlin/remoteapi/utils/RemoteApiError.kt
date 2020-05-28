package com.jetbrains.handson.mpp.mobile.remoteapi.utils

sealed class RemoteApiError
data class UnknownError(val code: Int) : RemoteApiError()
object NetworkError : RemoteApiError()
object ItemNotFoundError : RemoteApiError()