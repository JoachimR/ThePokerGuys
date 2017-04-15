package de.reiss.thepokerguys

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.download.DownloadItem
import de.reiss.thepokerguys.download.DownloadManagerHelper
import de.reiss.thepokerguys.list.RssDownloader
import de.reiss.thepokerguys.settings.AppSettings
import de.reiss.thepokerguys.util.RxUtil
import de.reiss.thepokerguys.util.view.TestableProgressBar

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
