package net.thepokerguys.util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.AppCompatDrawableManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spanned
import android.util.Pair
import android.view.View
import android.widget.ImageView

fun isConnectedToWiFi(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled && wifiManager.connectionInfo.networkId != -1
    } else {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
    }
}

fun setImageDrawable(context: Context, imageView: ImageView?, drawableResId: Int) {
    imageView?.setImageDrawable(getDrawableCompat(context, drawableResId))
}

fun getDrawableCompat(context: Context, drawableResId: Int): Drawable = AppCompatDrawableManager.get().getDrawable(context, drawableResId)

fun FragmentActivity.showDialogFragment(dialogFragment: DialogFragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(dialogFragment, dialogFragment.javaClass.simpleName)
    transaction.commit()
}

/**
 * Start a new Activity with a transition.
 *
 *
 * Will start the new Activity without transition if
 * - the Android version does not support it
 * - the transitionView is null
 * - the transitionId is null or empty

 * @param intent         the intent of the new Activity
 * *
 * @param requestCode    the request code if the new Activity should be started with, -1 if not needed
 * *
 * @param transitionList a list of Pairs that have the necessary information for a transition. The View
 * *                       is the starting view from which to start a transition. The String needs to be
 * *                       set to the transition id that the starting view and the target view have in
 * *                       common.
 */
fun Activity.startActivityWithTransition(intent: Intent, requestCode: Int,
                                         transitionList: List<Pair<View, String>>) {

    if (!transitionList.isEmpty() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        val opt: ActivityOptions

        if (transitionList.size == 1) {
            val pair = transitionList[0]
            opt = ActivityOptions.makeSceneTransitionAnimation(this, pair.first, pair.second)
        } else {
            val transitionArray = arrayOfNulls<Pair<View, String>>(transitionList.size)
            for (i in transitionList.indices) {
                val transition = transitionList[i]
                transitionArray[i] = transition
            }
            opt = ActivityOptions.makeSceneTransitionAnimation(this, *transitionArray)
        }

        this.startActivityForResult(intent, requestCode, opt.toBundle())
        return
    }
    this.startActivityForResult(intent, requestCode)
}

fun setFabBehavior(fab: FloatingActionButton?, behavior: CoordinatorLayout.Behavior<View>?) {
    if (fab != null) {
        val p = fab.layoutParams as CoordinatorLayout.LayoutParams
        p.behavior = behavior
        fab.layoutParams = p
    }
}

fun convertStringToHtmlText(s: String): Spanned {
    @Suppress("DEPRECATION")
    return android.text.Html.fromHtml(s)
}

fun setupRecyclerViewLayout(activity: Activity, recyclerView: RecyclerView?,
                            useGridLayoutOnWideScreen: Boolean) {
    if (recyclerView != null) {
        recyclerView.setHasFixedSize(true)
        if (useGridLayoutOnWideScreen && isAtLeast500dpWide(activity)) {
            recyclerView.layoutManager = GridLayoutManager(activity, 2,
                    GridLayoutManager.VERTICAL, false)
        } else {
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }
}

fun isAtLeast500dpWide(context: Context): Boolean {
    val displayMetrics = context.resources.displayMetrics
    return displayMetrics.widthPixels / displayMetrics.density >= 500
}

