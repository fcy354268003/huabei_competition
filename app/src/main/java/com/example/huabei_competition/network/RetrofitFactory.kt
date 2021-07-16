package com.example.huabei_competition.network

import com.example.huabei_competition.util.Logger
import com.example.huabei_competition.util.MyApplication
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    private const val DEFAULT_TIME_OUT = 10 * 1000L
    private const val BASE_URL = "http://192.168.115.65:8000"
    private val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(getDefInterceptor())
            .cache(getCache())
            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.MILLISECONDS)


    fun create(): Retrofit {
        val client = clientBuilder.build()
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
    }

    // 缓存设置为    50 M
    private fun getCache(): Cache =
            Cache(File(MyApplication.getApplicationByReflect().cacheDir, "cache"), 1024 * 1024 * 50)


    private fun getDefInterceptor(): Interceptor = DefInterceptor()

    private class DefInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Logger.i("${request.url} \n ${request.body.toString()} \n ${request.headers}")
            return chain.proceed(chain.request())
        }

    }
}