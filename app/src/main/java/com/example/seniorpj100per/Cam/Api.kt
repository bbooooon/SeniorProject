package com.example.seniorpj100per.Cam

import retrofit2.Call
import retrofit2.http.GET

public interface Api {
    @GET("run")
    fun getPrediction(): Call<Prediction>
}