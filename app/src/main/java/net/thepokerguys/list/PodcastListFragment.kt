package net.thepokerguys.list

import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.podcast_list_fragment.*
import net.thepokerguys.AppActivity
import net.thepokerguys.AppFragment
import net.thepokerguys.R
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.PlayButtonStateChanged
import net.thepokerguys.util.FragmentUtil.initPresenter


class PodcastListFragment : AppFragment(), PodcastList.View {

    lateinit private var presenter: PodcastList.Presenter

    private var deleteFileSnackbar: Snackbar? = null
    private var errorLoadingSnackbar: Snackbar? = null
    private var errorDownloadingSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.podcast_list_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter = initPresenter(childFragmentManager, PodcastListPresenter::class.java)
    }

    private lateinit var listItemAdapter: ListItemAdapter

    private fun initViews() {
        podcast_list_fragment_recycler_view.layoutManager = LinearLayoutManager(context)
        listItemAdapter = ListItemAdapter(activity as AppActivity)
        podcast_list_fragment_recycler_view.adapter = listItemAdapter

        podcast_list_fragment_swipe_to_refresh.setOnRefreshListener {
            if (!presenter.mvpModel.isDownloadingList) {
                presenter.downloadRssList()
            }
        }

        podcast_list_fragment_empty.visibility = GONE
        podcast_list_fragment_recycler_view.visibility = GONE
    }

    override fun setMVPModel(model: PodcastList.Model) {
        if (!isAdded) {
            return
        }
        activity?.runOnUiThread {

            podcast_list_fragment_swipe_to_refresh.isRefreshing = model.isReloadingList

            showSnackbarErrorDownloading(model.errorDownloadingRss)
            showSnackbarErrorLoading(model.errorDownloadingRss.not() && model.errorReloadingList)

            if (model.errorDownloadingRss.not() && model.errorReloadingList.not()) {
                setNonErrorModel(model)
            }

            setToolbarPlayOrPause(model)
        }
    }

    override fun updatePlayingInfo(url: String, currentPlayTime: Int, isSetToFile: Boolean) {
        if (!isAdded) {
            return
        }
        activity?.runOnUiThread {
            listItemAdapter.notifyPlayTimeChanged(url, currentPlayTime, isSetToFile)
        }
    }

    override fun showUndoDeleteSnackBar(originalTitle: String, originalFilePath: String) {
        if (!isAdded) {
            return
        }
        deleteFileSnackbar?.let {
            if (it.isShown) {
                it.dismiss()
            }
        }
        deleteFileSnackbar = Snackbar.make(podcast_list_fragment_recycler_view,
                context!!.getString(R.string.deleted, originalTitle), Snackbar.LENGTH_INDEFINITE)
                .addCallback(object : Snackbar.Callback() {

                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            deleteFileHelper().cleanupUndoDeleteCache()
                        }
                    }

                })
                .setAction(R.string.undo) {
                    deleteFileHelper().undoDeleteAudioFile(originalFilePath)
                }

        deleteFileSnackbar?.show()
    }

    private fun setToolbarPlayOrPause(model: PodcastList.Model) {
        AppEventBus.post(PlayButtonStateChanged(model.isSetToSomeFile, model.isCurrentlyPlaying))
    }

    private fun setNonErrorModel(model: PodcastList.Model) {
        when {
            model.listItems.isEmpty() -> {
                when {
                    model.isReloadingList ->
                        podcast_list_fragment_empty.visibility = GONE
                    else -> {
                        podcast_list_fragment_empty.visibility = VISIBLE
                        podcast_list_fragment_recycler_view.visibility = GONE
                    }
                }
            }
            else -> {
                podcast_list_fragment_empty.visibility = GONE
                podcast_list_fragment_recycler_view.visibility = VISIBLE

                listItemAdapter.update(model.listItems)
            }
        }
    }

    private fun showSnackbarErrorLoading(show: Boolean) {
        showOrHideSnackbar(
                doShow = show,
                currentSnackbar = errorLoadingSnackbar,
                createNewAndSetSnackbar = {
                    val created = Snackbar.make(podcast_list_fragment_recycler_view,
                            R.string.podcast_detail_error_loading, Snackbar.LENGTH_SHORT)
                    errorLoadingSnackbar = created
                    return@showOrHideSnackbar created
                })
    }

    private fun showSnackbarErrorDownloading(show: Boolean) {
        showOrHideSnackbar(
                doShow = show,
                currentSnackbar = errorDownloadingSnackbar,
                createNewAndSetSnackbar = {
                    val created = Snackbar.make(podcast_list_fragment_recycler_view,
                            R.string.podcast_detail_error_downloading, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.try_again, { presenter.downloadRssList() })
                    errorDownloadingSnackbar = created
                    return@showOrHideSnackbar created
                })
    }

    private fun showOrHideSnackbar(doShow: Boolean,
                                   currentSnackbar: Snackbar?,
                                   createNewAndSetSnackbar: () -> Snackbar) {
        if (currentSnackbar == null) {
            if (doShow) {
                createNewAndSetSnackbar().show()
            }
        } else {
            if (doShow) {
                if (currentSnackbar.isShown.not()) {
                    currentSnackbar.show()
                }
            } else {
                if (currentSnackbar.isShown) {
                    currentSnackbar.dismiss()
                }
            }
        }
    }

    override fun finish() {
        activity?.finish()
    }

    fun onTouchActionDownEvent(x: Float, y: Float) {

        deleteFileSnackbar?.let { snackbar ->

            if (snackbar.isShown && userHasTouchedOutsideOfSnackbarArea(snackbar, x, y)) {
                snackbar.dismiss()
            }
        }
    }

    private fun userHasTouchedOutsideOfSnackbarArea(
            snackbar: Snackbar,
            touchedScreenAtX: Float,
            touchedScreenAtY: Float): Boolean {
        val snackbarRect = Rect()
        snackbar.view.getHitRect(snackbarRect)
        return snackbarRect.contains(touchedScreenAtX.toInt(), touchedScreenAtY.toInt()).not()
    }

}