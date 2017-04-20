package net.thepokerguys

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import net.thepokerguys.database.DbProxy
import net.thepokerguys.download.DownloadItem
import net.thepokerguys.download.DownloadManagerHelper
import net.thepokerguys.list.RssDownloader
import net.thepokerguys.settings.AppSettings
import net.thepokerguys.util.RxUtil
import net.thepokerguys.util.view.TestableProgressBar

class TestApp : App() {

    override fun init() {
        appContext = applicationContext
        declareAsTest()
        setSingletons(defaultAppSettings())
    }

    private fun declareAsTest() {
        RxUtil.setTestMode(true)
        TestableProgressBar.isTestRunning = true
    }

    fun setSingletons(appSettings: AppSettings = defaultAppSettings(),
                      mockedDbProxy: DbProxy = defaultMockDbProxy(),
                      mockedDownloadManagerHelper: DownloadManagerHelper = defaultMockDownloadManagerHelper(),
                      rssDownloader: RssDownloader = defaultMockRssDownloader()) {

        appSingletons = mock {

            on { appSettings() } doReturn appSettings

            on { dbProxy() } doReturn mockedDbProxy

            on { downloadManagerHelper() } doReturn mockedDownloadManagerHelper

            on { rssDownloader() } doReturn rssDownloader
        }
    }

    fun defaultAppSettings(): AppSettings {
        return mock()
    }

    fun defaultMockDbProxy(): DbProxy {
        return mock()
    }

    fun defaultMockDownloadManagerHelper(): DownloadManagerHelper {
        return mock {
            on { allCurrentDownloadItems() } doReturn emptyList<DownloadItem>()
        }
    }

    fun defaultMockRssDownloader(): RssDownloader {
        return mock()
    }

}
