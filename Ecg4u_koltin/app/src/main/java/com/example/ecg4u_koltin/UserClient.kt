package com.example.ecg4u_koltin

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
interface UserClient {
    @Multipart
    @POST("predict")
    fun uploadFile(
        @Part photo : MultipartBody.Part
    ) : Call<ResponseBody>
}
