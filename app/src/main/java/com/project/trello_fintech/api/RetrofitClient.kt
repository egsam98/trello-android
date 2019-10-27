package com.project.trello_fintech.api


import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.project.trello_fintech.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.utils.StringsRepository
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 * Клиент для осуществления CRUD
 */
object RetrofitClient {

    val _retrofitBuilder: Retrofit.Builder by lazy {
        val httpClient = OkHttpClient.Builder()
            .cache(MainActivity.cache)
            .addInterceptor {
                val token = StringsRepository.get("token")
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

    fun getAuthUrl(cxt: Context): String {
        val expiration = "30days"
        return "${BuildConfig.TRELLO_BASE_URL}authorize?" +
                "expiration=$expiration&" +
                "name=${cxt.resources.getString(R.string.app_name)}&" +
                "callback_method=fragment&" +
                "scope=read,write&" +
                "response_type=token&" +
                "key=${BuildConfig.TRELLO_API_KEY}&" +
                "return_url=${BuildConfig.TRELLO_URL_CALLBACK}"
    }

    inline fun <reified T> create(): T {
        val rxJava2Adapter = RxJava2Adapter(AndroidSchedulers.mainThread())
        return _retrofitBuilder
            .addCallAdapterFactory(rxJava2Adapter)
            .build()
            .create(T::class.java)
    }
}