package com.project.trello_fintech.api

import android.util.Log
import com.google.gson.GsonBuilder
import com.project.trello_fintech.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.utils.StringsRepository
import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.Cache


/**
 * Клиент для осуществления CRUD
 * @property _retrofitBuilder Builder
 */
class RetrofitClient(cache: Cache, stringsRepository: StringsRepository) {

    val _retrofitBuilder: Retrofit.Builder by lazy {
        val httpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor {
                val token = stringsRepository.get("token")
                val request = it.request()
                val modifiedUrl = request.url()
                    .newBuilder()
                    .addQueryParameter("token", token)
                    .addQueryParameter("key", BuildConfig.TRELLO_API_KEY)
                    .build()

                val modifiedRequest = request.newBuilder()
                    .url(modifiedUrl)
                    .build()

                Log.i("${request.method()} URL", modifiedUrl.toString())

                it.proceed(modifiedRequest)
            }
            .build()

        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.TRELLO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    inline fun <reified T> create(onError: LiveEvent<Pair<String, Int?>>): T {
        val rxJava2Adapter = RxJava2Adapter(AndroidSchedulers.mainThread(), onError)
        return _retrofitBuilder
            .addCallAdapterFactory(rxJava2Adapter)
            .build()
            .create(T::class.java)
    }
}