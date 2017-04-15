package de.reiss.thepokerguys

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import de.reiss.thepokerguys.audio.AudioPlayerService
import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.delete.DeleteFileHelper
import de.reiss.thepokerguys.download.DownloadManagerHelper
import de.reiss.thepokerguys.list.RssDownloader
import de.reiss.thepokerguys.settings.AppSettings
import de.reiss.thepokerguys.util.logWarn

open class App : Application() {

    lateinit var appContext: Context
    lateinit var appSingletons: AppSingletons

    override fun onCreate() {
        super.onCreate()
        bindToAudioPlayService()
        init()
    }

    open fun init() {
        appContext = applicationContext

        appSingletons = object : AppSingletons {

            private var appSettings: AppSettings? = null
            private var dbProxy: DbProxy? = null
            private var downloadManagerHelper: DownloadManagerHelper? = null
            private var deleteFileHelper: DeleteFileHelper? = null
            private var rssDownloader: RssDownloader? = null

            override fun appSettings(): AppSettings {
                if (appSettings == null) {
                    appSettings = AppSettings(appContext)
                }
                return appSettings!!
            }

            override fun dbProxy(): DbProxy {
                if (dbProxy == null) {
                    dbProxy = DbProxy(appContext)
                }
                return dbProxy!!
            }

            override fun downloadManagerHelper(): DownloadManagerHelper {
                if (downloadManagerHelper == null) {
                    downloadManagerHelper = DownloadManagerHelper(appContext)
                }
                return downloadManagerHelper!!
            }

            override fun deleteFileHelper(): DeleteFileHelper {
                if (deleteFileHelper == null) {
                    deleteFileHelper = DeleteFileHelper(appContext)
                }
                return deleteFileHelper!!
            }

            override fun rssDownloader(): RssDownloader {
                if (rssDownloader == null) {
                    rssDownloader = RssDownloader(appContext)
                }
                return rssDownloader!!
            }

        }
    }

    fun bindToAudioPlayService() {
        val playIntent = Intent(this, AudioPlayerService::class.java)
        bindService(playIntent, object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                if (service is AudioPlayerService.LocalBinder) {
                    val binder = service
                    AudioPlayerService.instance = binder.service
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                AudioPlayerService.instance?.stopSelf()
            }
        }, Context.BIND_AUTO_CREATE)
    }

    val appInfo: String
        get() {
            return getString(R.string.app_info_text,
                    formattedAppVersion,
                    getString(R.string.info),
                    getString(R.string.info_open_source),
                    getString(R.string.developer_prefix),
                    getString(R.string.developer),
                    getString(R.string.developer_mail),
                    getString(R.string.report_bugs))
        }

    val formattedAppVersion: String
        get() {
            try {
                val version = packageManager.getPackageInfo(packageName, 0).versionName
                if (version != null && version.isNotEmpty()) {
                    return getString(R.string.app_info_version, version)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                logWarn(e) { "Could not get app version" }
            }
            return " "
        }

}