package de.reiss.thepokerguys

import android.content.Intent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import de.reiss.thepokerguys.util.hasAllPermissions


abstract class WithPermissionActivity : RxAppCompatActivity() {

    override fun onStart() {
        super.onStart()
        redirectToPermissionCheckIfNecessary()
    }

    fun redirectToPermissionCheckIfNecessary(): Boolean {
        if (!hasAllPermissions(this)) {
            startActivityForResult(Intent(this, AskForPermissionActivity::class.java), 1)
            return true
        }
        return false
    }

}
