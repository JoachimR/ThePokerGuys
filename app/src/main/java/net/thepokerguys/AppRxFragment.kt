package net.thepokerguys

import com.trello.rxlifecycle.components.support.RxFragment

abstract class AppRxFragment : RxFragment(), AppSingletons {

    protected fun app() = (context?.applicationContext as App)

    override fun appSettings() = app().appSingletons.appSettings()

    override fun dbProxy() = app().appSingletons.dbProxy()

    override fun downloadManagerHelper() = app().appSingletons.downloadManagerHelper()

    override fun deleteFileHelper() = app().appSingletons.deleteFileHelper()

    override fun rssDownloader() = app().appSingletons.rssDownloader()

}