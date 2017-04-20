package net.thepokerguys.util

import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers.mainThread
import rx.schedulers.Schedulers
import rx.schedulers.Schedulers.immediate
import rx.schedulers.Schedulers.io

object RxUtil {

    private var isTest: Boolean = false

    fun setTestMode(isTest: Boolean) {
        this.isTest = isTest
    }

    private val ioMainTransformer = Observable.Transformer<Any, Any> { observable ->
        observable.subscribeOn(io()).observeOn(mainThread())
    }

    private val sameThreadTransformer = Observable.Transformer<Any, Any> { observable ->
        observable.subscribeOn(Schedulers.immediate()).observeOn(immediate())
    }

    private val newThreadTransformer = Observable.Transformer<Any, Any> { observable ->
        observable.subscribeOn(Schedulers.newThread()).observeOn(mainThread())
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> ioMain(): Observable.Transformer<T, T> {
        if (isTest) {
            return sameThreadTransformer as Observable.Transformer<T, T>
        }
        return ioMainTransformer as Observable.Transformer<T, T>
    }

    fun isLoading(subscription: Subscription?): Boolean {
        return subscription != null && !subscription.isUnsubscribed
    }

    fun cancel(subscription: Subscription?): Subscription? {
        subscription?.unsubscribe()
        return null
    }

}