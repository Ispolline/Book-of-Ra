@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.abdulahad.bookravulk.retrofit

import com.abdulahad.bookravulk.*
import com.abdulahad.bookravulk.`interface`.RetrofitServices
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {

    var BASE_URL:String="http://bookravulk.space/"
    var BASE_HTTPS_URL:String="https://bookravulk.space/"

    val getClient: RetrofitServices
        get() {

            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = UserAgentInterceptor(System.getProperty("http.agent"))

            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(RetrofitServices::class.java)

        }

    val getHttpsClient: RetrofitServices
        get() {


            val gson = GsonBuilder()
                .setLenient()
                .create()


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_HTTPS_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
            return retrofit.create(RetrofitServices::class.java)
        }

    private class UserAgentInterceptor(private val userAgent: String) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()
            val requestWithUserAgent: Request = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
            return chain.proceed(requestWithUserAgent)
        }
    }

}


object ApiClientSmsGorod {

    var BASE_URL:String="https://new.smsgorod.ru/apiSms/"

    val getClient: RetrofitServices
        get() {

            val gson = GsonBuilder()
                .setLenient()
                .create()


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()

            return retrofit.create(RetrofitServices::class.java)

        }

}
