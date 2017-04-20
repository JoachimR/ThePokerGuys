package net.thepokerguys.list

import android.os.Bundle
import net.thepokerguys.AppRxFragment
import net.thepokerguys.audio.AudioPlayerService
import net.thepokerguys.database.PodcastDatabaseItem
import net.thepokerguys.download.DownloadFolder
import net.thepokerguys.download.DownloadItem
import net.thepokerguys.download.DownloadManagerHelper
import net.thepokerguys.eventbus.*
import net.thepokerguys.util.RxUtil
import net.thepokerguys.util.logWarn
import org.greenrobot.eventbus.Subscribe
import rx.Observable
import rx.Subscription
import java.util.*

open class PodcastListPresenter : AppRxFragment(), PodcastList.Presenter {

    private val RECENT_DAYS_IN_THE_PAST = 3

    private var downloadSubscription: Subscription? = null
    private var readFromDatabaseSubscription: Subscription? = null
    private var buildListSubscription: Subscription? = null

    private val model = PodcastList.Model()

    private var databaseList: List<PodcastDatabaseItem> = ArrayList()

    override val mvpView: PodcastList.View?
        get() {
            return parentFragment as PodcastList.View?
        }

    override val mvpModel: PodcastList.Model
        get() = model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onStart() {
        super.onStart()
        AppEventBus.register(this)

        updatePlayStateInModel()
        refreshFromDb()
    }

    override fun onStop() {
        AppEventBus.unregister(this)
        super.onStop()
    }

    @Subscribe
    fun onEvent(event: DownloadStopped) {
        refreshFromDb(silent = true)
    }

    @Subscribe
    fun onEvent(event: DownloadStarted) {
        refreshFromDb(silent = true)
    }

    @Subscribe
    fun onEvent(event: Mp3FileProgress) {
        updatePlayStateInModel()
        refreshModelInView()
        mvpView?.updatePlayingInfo(
                url = event.url,
                currentPlayTime = event.progressInMs,
                isSetToFile = true)
    }

    @Subscribe
    fun onEvent(event: FileDeleted) {
        refreshFromDb(silent = true)
        mvpView?.showUndoDeleteSnackBar(event.title, event.filePath)
    }

    @Subscribe
    fun onEvent(event: FileRestored) {
        refreshFromDb(silent = true)
    }

    @Subscribe
    fun onEvent(event: AudioFilePlaybackChanged) {
        updatePlayStateInModel()
        refreshFromDb(silent = true)
    }

    override fun downloadRssList() {
        downloadSubscription?.let {
            if (!it.isUnsubscribed) return  // already downloading
        }

        model.isReloadingList = true
        model.isDownloadingList = true
        model.errorReloadingList = false
        model.errorDownloadingRss = false
        refreshModelInView()

        downloadSubscription = downloadNewItems()
                .compose(RxUtil.ioMain())
                .subscribe({
                    model.isDownloadingList = false
                    refreshFromDb()
                }, { error ->
                    logWarn(error) { "Could not download and parse RSS list successfully" }
                    model.isReloadingList = false
                    model.errorDownloadingRss = true

                    model.isDownloadingList = false
                    refreshModelInView()
                })
    }

    fun refreshFromDb(silent: Boolean = false) {
        readFromDatabaseSubscription?.let {
            if (!it.isUnsubscribed) return  // already reading
        }

        if (!silent) {
            model.isReloadingList = true
            model.errorReloadingList = false
            refreshModelInView()
        }

        readFromDatabaseSubscription = dbProxy().loadAllPodcastDatabaseItemsObservable()
                .compose(RxUtil.ioMain<List<PodcastDatabaseItem>>())
                .doAfterTerminate {
                    refreshList()
                }
                .subscribe({ result ->
                    databaseList = result
                }, { error ->
                    logWarn(error) { "Could not read from database successfully" }
                    model.isReloadingList = false
                    model.errorReloadingList = true
                })
    }

    private fun refreshList() {
        buildListSubscription?.let {
            if (!it.isUnsubscribed) return  // already refreshing
        }

        buildListSubscription = buildListFromDatabaseList()
                .compose(RxUtil.ioMain<ArrayList<ListItem>>())
                .doAfterTerminate {
                    model.isReloadingList = false
                    refreshModelInView()
                }
                .subscribe({ result ->
                    model.listItems = result
                }, { error ->
                    logWarn(error) { "Could not build list successfully" }
                    model.errorReloadingList = true
                })
    }

    open fun buildListFromDatabaseList(): Observable<ArrayList<ListItem>> {
        return Observable.create {
            try {
                val allCurrentDownloads = downloadManagerHelper().allCurrentDownloadItems()
                val result = ArrayList<ListItem>()
                databaseList.forEach { dbItem ->
                    result.add(createListItem(
                            dbItem = dbItem,
                            currentDownloadItem = findCurrentDownloadItem(dbItem.url, allCurrentDownloads)))
                }
                it.onNext(result)
                it.onCompleted()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    fun findCurrentDownloadItem(url: String, fromList: List<DownloadItem>): DownloadItem? {
        val urlIdentifier = DownloadManagerHelper.urlIdentifier(url)
        if (urlIdentifier.isEmpty()) {
            return null
        }
        fromList.forEach {
            if (DownloadManagerHelper.urlIdentifier(it.url) == urlIdentifier) {
                return it
            }
        }
        return null
    }

    private fun createListItem(dbItem: PodcastDatabaseItem,
                               currentDownloadItem: DownloadItem?): ListItem {
        return ListItem(
                title = dbItem.title,
                description = dbItem.description,
                url = dbItem.url,
                currentPlayTime = dbItem.progress,
                maxPlayTime = dbItem.duration,
                publishedDate = dbItem.publishedDate,
                isFullyDownloaded = currentDownloadItem == null && doesFileExist(dbItem.url),
                isCurrentlyDownloading = currentDownloadItem != null,
                downloadProgress = currentDownloadItem?.percentProgress() ?: -1,
                isPlayerSetToFile = isSetToFile(dbItem.url),
                wasRecentlyPublished = wasRecentlyPublished(dbItem.publishedDate))
    }

    private fun doesFileExist(url: String): Boolean {
        val file = DownloadFolder.findFile(url)
        return file != null && file.exists()
    }

    private fun downloadNewItems(): Observable<Void?> {
        return Observable.create {
            try {
                val podcastItems = rssDownloader().execute()

                dbProxy().insertOrUpdatePodcasts(
                        podcastItems = podcastItems,
                        keepPlayProgress = true)
                it.onNext(null)
                it.onCompleted()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    private fun updatePlayStateInModel() {
        model.isSetToSomeFile = audioPlayer()?.isSetToSomeExistingFile() ?: false
        model.isCurrentlyPlaying = model.isSetToSomeFile && audioPlayer()?.isPlayingCurrent() ?: false
    }

    private fun refreshModelInView() {
        mvpView?.setMVPModel(model)
    }

    fun isSetToFile(url: String): Boolean {
        val file = DownloadFolder.fileForUrl(url)
        return audioPlayer()?.isSetToFile(file.absolutePath) ?: false
    }

    private fun wasRecentlyPublished(publishedDate: Date): Boolean {
        val recently = Calendar.getInstance()
        recently.add(Calendar.DAY_OF_YEAR, -RECENT_DAYS_IN_THE_PAST)
        return publishedDate.after(recently.time)
    }

    private fun audioPlayer() = AudioPlayerService.instance

}
