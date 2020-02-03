package com.project.trello_fintech.adapters

import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.IllegalArgumentException
import java.lang.reflect.Type


/**
 * Адаптер, предоставляющий глобальный обработчик ошибок и обеспечивающий выполнения нисходящего потока (downstream) в
 * предпочитаемом Scheduler
 * @property observeOn Scheduler
 * @property original (retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory..retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory?)
 */
class RxJava2Adapter(
        private val observeOn: Scheduler,
        private val onError: LiveEvent<Pair<String, Int?>>? = null
    ): CallAdapter.Factory() {

    private val original = RxJava2CallAdapterFactory.createAsync()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<*, *>
        return RxCallAdapterWrapper(wrapped)
    }

    private inner class RxCallAdapterWrapper<R>(private val wrapped: CallAdapter<R, *>): CallAdapter<R, Any> {
        override fun adapt(call: Call<R>): Any {
            val source = wrapped.adapt(call)
            return when (source) {
                is Observable<*> -> source.observeOn(observeOn)
                    .doOnError{ emitError(it) }
                    .onErrorResumeNext(Observable.empty())
                is Flowable<*> -> source.observeOn(observeOn)
                    .doOnError { emitError(it) }
                    .onErrorResumeNext(Flowable.empty())
                is Single<*> -> source.observeOn(observeOn)
                    .doOnError { emitError(it) }
                    .onErrorResumeNext(Single.never())
                is Completable -> source.observeOn(observeOn)
                    .doOnError { emitError(it) }
                    .onErrorComplete()
                is Maybe<*> -> source.observeOn(observeOn)
                    .doOnError { emitError(it) }
                    .onErrorResumeNext(Maybe.empty())
                else -> throw IllegalArgumentException("Unknown reactive source")
            }
        }

        private fun emitError(t: Throwable) {
            val messageAndCode: Pair<String, Int?> = when (t) {
                is HttpException -> {
                    val message = t.response()?.errorBody()?.string() ?: t.response()?.message() ?: t.message()
                    Pair(message, t.code())
                }
                else -> Pair(t.message.orEmpty(), null)
            }
            onError?.emit(messageAndCode)
        }

        override fun responseType(): Type = wrapped.responseType()
    }
}