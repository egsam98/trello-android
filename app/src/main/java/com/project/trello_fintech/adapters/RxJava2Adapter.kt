package com.project.trello_fintech.adapters

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type
import io.reactivex.Scheduler


/**
 * Адаптер, предоставляющий глобальный обработчик ошибок и обеспечивающий выполнения нисходящего потока (downstream) в
 * предпочитаемом Scheduler
 * @property observeOn Scheduler
 * @property original (retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory..retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory?)
 */
class RxJava2Adapter(
        private val observeOn: Scheduler
    ): CallAdapter.Factory() {

    companion object {
        var errorHandler: ((Throwable) -> Unit)? = null
    }

    private val original = RxJava2CallAdapterFactory.createAsync()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<*, *>
        return RxCallAdapterWrapper(wrapped)
    }

    private inner class RxCallAdapterWrapper<R>(private val wrapped: CallAdapter<R, *>): CallAdapter<R, Any> {
        override fun adapt(call: Call<R>): Any = (wrapped.adapt(call) as Observable<*>)
            .observeOn(observeOn)
            .doOnError{ errorHandler?.invoke(it) }
            .onErrorResumeNext(Observable.empty())

        override fun responseType(): Type = wrapped.responseType()
    }
}