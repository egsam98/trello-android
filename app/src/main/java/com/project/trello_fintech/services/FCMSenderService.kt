package com.project.trello_fintech.services

import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.models.firebase.FirebaseMessage
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Firebase Cloud Messaging Sender
 * @property fcmApi
 */
@Singleton
class FCMSenderService @Inject constructor(
        gsonConverterFactory: GsonConverterFactory,
        loggingInterceptor: HttpLoggingInterceptor
    ) {

    private interface FCMApi {
        @POST("send")
        fun send(@Body firebaseMessage: FirebaseMessage): Completable
    }

    private val fcmApi by lazy {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor {
                val modifiedReq = it.request().newBuilder()
                    .header("Authorization", "key=${BuildConfig.FIREBASE_CLOUD_MESSAGING_SERVER_KEY}")
                    .build()
                it.proceed(modifiedReq)
            }
            .addInterceptor(loggingInterceptor)
            .build()
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2Adapter(AndroidSchedulers.mainThread()))
            .baseUrl(BuildConfig.FIREBASE_CLOUD_MESSAGING_SERVER_URL)
            .client(httpClient)
            .build()
            .create(FCMApi::class.java)
    }

    fun send(msg: FirebaseMessage) {
        fcmApi.send(msg).subscribe()
    }
}