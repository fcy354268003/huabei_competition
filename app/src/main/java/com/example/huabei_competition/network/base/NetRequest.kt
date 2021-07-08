package com.example.huabei_competition.network.base

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 * DCL实现单例
 */
class NetRequest private constructor() {
    private val mClient: OkHttpClient
    private val mHandler: Handler

    var callTimeOut: Long = 10
    var readTimeout: Long = 10
    var writeTimeout: Long = 10

    init {
        mClient = OkHttpClient.Builder()
                .callTimeout(callTimeOut, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addInterceptor(DefaultInterceptor())
                .build()
        mHandler = Handler(Looper.getMainLooper())
    }

    companion object {
        @JvmStatic
        private val instance: NetRequest by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NetRequest()
        }

        // get方法请求
        @JvmStatic
        fun getFormRequest(url: String, params: Map<String, String>?, callback: NetRequestCallback) {
            instance.innerGetFromAsync(url, params, callback)
        }

        //post方法请求
        @JvmStatic
        fun postFromRequest(url: String, params: Map<String, String>?, callback: NetRequestCallback) {
            instance.innerPostFromAsync(url, params, callback)
        }
    }

    /**
     *  url:        请求地址
     *  params:     参数
     *  callback:   回调
     */
    private fun innerGetFromAsync(url: String, params: Map<String, String>?, callback: NetRequestCallback) {
        val joinToUrl = joinToUrl(url, params)
        val request = Request.Builder().url(joinToUrl).build()
        val newCall = mClient.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                deliverDataFailure(request, e, callback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    deliverDataSuccess(response.body?.string(), callback)
                } else {
                    // TODO 打印日志
                    throw IllegalMonitorStateException("请求失败")
                }

            }

        })
    }

    private fun innerPostFromAsync(url: String, params: Map<String, String>?, callback: NetRequestCallback) {
        val bodyBuilder = FormBody.Builder()
        params?.forEach {
            bodyBuilder.add(it.key, it.value)
        }
        val formBody = bodyBuilder.build()
        val request = Request.Builder().url(url).post(body = formBody).build()
        mClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                deliverDataFailure(call.request(), exception = e, callback)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful)
                    deliverDataSuccess(response = response.body?.string(), callback)
                else deliverDataFailure(request, IllegalStateException("请求失败"), callback)
            }

        })
    }

    /**
     *
     *      json post 请求参数
     *
     */
    private fun buildJsonPostRequest(url: String, json: String): Request {
        val requestBody = RequestBody.create("application/json;charset=utf-8".toMediaTypeOrNull(), json)
        return Request.Builder().url(url).post(requestBody).build()
    }

    /**
     * 将参数添加到url后边
     */
    private fun joinToUrl(url: String, params: Map<String, String>?): String {
        val sb = StringBuilder()
        sb.append(url)
        var isFirst = true
        params?.forEach {
            if (isFirst) {
                isFirst = !isFirst
                sb.append("?")
            } else sb.append("&")
            sb.append(it.key)
            sb.append("=")
            sb.append(it.value)
        }
        return sb.toString()
    }

    /**
     * 将成功结果 送入 主线程队列
     */
    private fun deliverDataSuccess(response: String?, callback: NetRequestCallback) {
        mHandler.post {
            callback.success(result = response)
        }
    }

    /**
     * 将失败结果 送入 主线程队列
     */
    private fun deliverDataFailure(result: Request, exception: Exception, callback: NetRequestCallback) {
        mHandler.post {
            callback.failure(request = result, exception = exception)
        }
    }

    /**
     * 默认拦截器 添加统一的头部
     */
    class DefaultInterceptor : Interceptor {
        companion object {
            @JvmField
            var token: String? = null
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder().apply {
                addHeader("os", "android")
                addHeader("appVersion", "0.1.2")
                token?.let {
                    addHeader("token", it)
                }
            }
            return chain.proceed(builder.build())
        }

    }
}

/**
 * 回调接口  runOnUiThread
 */

interface NetRequestCallback {
    fun success(result: String?)
    fun failure(request: Request, exception: Exception)
}