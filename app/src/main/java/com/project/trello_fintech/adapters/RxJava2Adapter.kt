package com.project.trello_fintech.adapters

import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type
import io.reactivex.Scheduler
import retrofit2.HttpException


/**
 * Адаптер, предоставляющий глобальный обработчик ошибок и обеспечивающий выполнения нисходящего потока (downstream) в
 * предпочитаемом Scheduler
 * @property observeOn Scheduler
 * @property original (retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory..retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory?)
 */
class RxJava2Adapter(
        private val observeOn: Scheduler,
        private val onError: LiveEvent<Pair<String, Int?>>
    ): CallAdapter.Factory() {

    private val original = RxJava2CallAdapterFactory.createAsync()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<*, *>
        return RxCallAdapterWrapper(wrapped)
    }

    private inner class RxCallAdapterWrapper<R>(private val wrapped: CallAdapter<R, *>): CallAdapter<R, Any> {
        override fun adapt(call: Call<R>): Any = (wrapped.adapt(call) as Observable<*>)
            .observeOn(observeOn)
            .doOnError {
                val messageAndCode: Pair<String, Int?> = when (it) {
                    is HttpException -> {
                        val message = it.response()?.errorBody()?.string() ?: it.response()?.message() ?: it.message()
                        Pair(message, it.code())
                    }
                    else -> Pair(it.message.orEmpty(), null)
                }
                onError.emit(messageAndCode)
            }
            .onErrorResumeNext(Observable.empty())

        override fun responseType(): Type = wrapped.responseType()
    }
}