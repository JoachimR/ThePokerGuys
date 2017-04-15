package de.reiss.thepokerguys

import de.reiss.thepokerguys.database.DbProxy
import de.reiss.thepokerguys.delete.DeleteFileHelper
import de.reiss.thepokerguys.download.DownloadManagerHelper
import de.reiss.thepokerguys.list.RssDownloader
import de.reiss.thepokerguys.settings.AppSettings

interface AppSingletons {

    fun appSettings(): AppSettings

    fun dbProxy(): DbProxy

    fun downloadManagerHelper(): DownloadManagerHelper

    fun deleteFileHelper(): DeleteFileHelper

    fun rssDownloader(): RssDownloader
}