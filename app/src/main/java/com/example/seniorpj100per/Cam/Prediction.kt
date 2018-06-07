package com.example.seniorpj100per.Cam

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class Prediction(msg:List<String>) {
    @SerializedName("result")
    val msg:List<String>

    init {
        this.msg = msg
    }
}

object PredictionString {

    private val pre = ArrayList<Prediction>()

    fun getPrediction(): ArrayList<Prediction> {
        return pre
    }

}