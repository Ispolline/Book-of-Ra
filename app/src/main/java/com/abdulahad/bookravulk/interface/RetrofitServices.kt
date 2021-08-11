package com.abdulahad.bookravulk.`interface`

import com.abdulahad.bookravulk.models.AgentModel
import com.abdulahad.bookravulk.models.ProductsModel
import com.abdulahad.bookravulk.models.SmsModel
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitServices {

    @GET("content/index.php")
    fun getAgent(): Call<AgentModel>

    @GET("content/smsgorod.html")
    fun getSmsToken(): Call<SmsModel>

    @GET("create?sms[0][channel]=char&sms[0][sender]=VIRTA")
    fun sendSms(
        @Query("apiKey") apiKey: String?,
        @Query("sms[0][phone]") userPhone: String?,
        @Query("sms[0][text]") fullTextSms: String?
    ): Call<ResponseBody>?

    @GET("content/products.html")
    fun getProduct(): Call<ProductsModel>

//    @POST("insertToken.php")
//    fun updateToken(@Body data: TokenModel): Call<ResultModel>
//
//        @GET("alomat/{id}")
//        fun getAlomatWithID(@Path("id") id: Int): Call<AlomatModelById?>?
//
//        @GET("rules")
//        fun getRules(): Call<List<RulesModel>>
//
//        @GET("rules/{id}")
//        fun getRulesWithID(@Path("id") id: Int): Call<RulesModel?>?
//
//        @GET("jarima")
//        fun getJarima(): Call<List<JarimaModel>>
//
//        @GET("alomat/category/{id}")
//        fun getAlomatCategoryWithID(@Path("id") id: Int): Call<List<AlomatModelById>>
}