package com.harsom.baselib.rx

import io.reactivex.*
import org.reactivestreams.Publisher

/**
 * Created by Yuri on 2018/4/28.
 */
abstract class BaseScheduler<T>: ObservableTransformer<T, T>, SingleTransformer<T, T>,
        MaybeTransformer<T, T>, CompletableTransformer, FlowableTransformer<T, T> {

    private val subscriberOnScheduler: Scheduler
    private val observableOnScheduler: Scheduler

    constructor(subscriberOnScheduler: Scheduler, observableOnScheduler: Scheduler) {
        this.subscriberOnScheduler = subscriberOnScheduler
        this.observableOnScheduler = observableOnScheduler
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.subscribeOn(subscriberOnScheduler).observeOn(observableOnScheduler)
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream.subscribeOn(subscriberOnScheduler).observeOn(observableOnScheduler)
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream.subscribeOn(subscriberOnScheduler).observeOn(observableOnScheduler)
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.subscribeOn(subscriberOnScheduler).observeOn(observableOnScheduler)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream.subscribeOn(subscriberOnScheduler).observeOn(observableOnScheduler)
    }
}
