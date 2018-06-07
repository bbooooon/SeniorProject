package com.example.seniorpj100per.Healthy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Smew on 25/2/2561.
 */

public interface ServiceXML {

    @GET
    Call<RssFeed> getRss(@Url String url);
}
