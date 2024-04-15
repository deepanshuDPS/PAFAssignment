package com.paf.assignment.network.interceptors

import okhttp3.*


class PAFApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url().newBuilder().build()
        val request = original.newBuilder()
            .addHeader("Accept-Version", "v1")
            .method(original.method(), original.body())
            .url(originalHttpUrl)
            .build()
        return chain.proceed(request)
    }
}