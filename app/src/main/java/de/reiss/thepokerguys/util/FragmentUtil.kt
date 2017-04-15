package de.reiss.thepokerguys.util

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

object FragmentUtil {

    fun showDialogFromFragment(fragment: Fragment, dialogFragment: DialogFragment) {
        val transaction = fragment.childFragmentManager.beginTransaction()
        transaction.add(dialogFragment, dialogFragment.javaClass.simpleName)
        transaction.commit()
    }

    @JvmOverloads fun showDialog(fragmentActivity: FragmentActivity, dialogFragment: DialogFragment, tag: String = dialogFragment.javaClass.simpleName) {
        val transaction = fragmentActivity.supportFragmentManager.beginTransaction()
        transaction.add(dialogFragment, tag)
        transaction.commit()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Fragment> initPresenter(fragmentManager: FragmentManager, clazz: Class<T>): T {
        val tag = clazz.simpleName
        var presenter: T? = fragmentManager.findFragmentByTag(tag) as T?
        if (presenter == null) {
            try {
                presenter = clazz.newInstance()
            } catch (e: InstantiationException) {
                throw RuntimeException("Could not instantiate presenter fragment " + tag, e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Could not instantiate presenter fragment " + tag, e)
            }

            fragmentManager.beginTransaction()
                    .add(presenter, tag)
                    .commitNow()
        }
        return presenter!!
    }

}
