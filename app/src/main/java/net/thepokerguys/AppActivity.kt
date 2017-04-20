package net.thepokerguys


abstract class AppActivity : WithPermissionActivity(), AppSingletons {

    fun app() = (application as App)

    override fun appSettings() = app().appSingletons.appSettings()

    override fun dbProxy() = app().appSingletons.dbProxy()

    override fun downloadManagerHelper() = app().appSingletons.downloadManagerHelper()

    override fun deleteFileHelper() = app().appSingletons.deleteFileHelper()

    override fun rssDownloader() = app().appSingletons.rssDownloader()

}
