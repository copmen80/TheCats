package ua.devserhii.thecat

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object CatApiClient {

    private const val BASE_URL = "https://api.thecatapi.com"

    val API_INTERFACE_CLIENT: CatApiInterface by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return@lazy retrofit.create(CatApiInterface::class.java)
    }
}