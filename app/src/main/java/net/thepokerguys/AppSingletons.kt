package net.thepokerguys

import net.thepokerguys.database.DbProxy
import net.thepokerguys.delete.DeleteFileHelper
import net.thepokerguys.download.DownloadManagerHelper
import net.thepokerguys.list.RssDownloader
import net.thepokerguys.settings.AppSettings

interface AppSingletons {

    fun appSettings(): AppSettings

    fun dbProxy(): DbProxy

    fun downloadManagerHelper(): DownloadManagerHelper

    fun deleteFileHelper(): DeleteFileHelper

    fun rssDownloader(): RssDownloader
}