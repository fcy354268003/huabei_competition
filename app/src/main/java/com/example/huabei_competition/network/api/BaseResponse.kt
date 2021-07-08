package com.example.huabei_competition.network.api

data class BaseResponse<T>(val code: String,
                           val message: String,
                           val data: T?)

/**
 * 接收只有token的返回对象
 */
data class TokenOnly(val token: String)