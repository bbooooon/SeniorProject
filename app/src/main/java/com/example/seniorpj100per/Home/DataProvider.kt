package com.example.seniorpj100per.Home

import java.util.ArrayList

/**
 * Created by Smew on 28/1/2561.
 */

object DataProvider {

    private val data = ArrayList<HomeResult>()

    fun getData(): ArrayList<HomeResult> {
        return data
    }

    init {
    }

}