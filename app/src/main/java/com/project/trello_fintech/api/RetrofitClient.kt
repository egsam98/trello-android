package com.project.trello_fintech.api

import com.project.trello_fintech.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.utils.StringsRepository
import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.create
import javax.inject.Inject


/**
 * Клиент для осуществления CRUD
 * @property retrofitBuilder Builder
 */
class RetrofitClient @Inject constructor(
    cache: Cache,
    stringsRepository: StringsRepository,
    loggingInterceptor: HttpLoggingInterceptor,
    gsonConverterFactory: GsonConverterFactory) {

    val retrofitBuilder: Retrofit.Builder by lazy {
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

                it.proceed(modifiedRequest)
            }
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.TRELLO_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
    }

    inline fun <reified T> create(
        onError: LiveEvent<Pair<String, Int?>>? = null,
        scheduler: Scheduler = AndroidSchedulers.mainThread()): T {

        val rxJava2Adapter = RxJava2Adapter(scheduler, onError)
        return retrofitBuilder
            .addCallAdapterFactory(rxJava2Adapter)
            .build()
            .create()
    }
}