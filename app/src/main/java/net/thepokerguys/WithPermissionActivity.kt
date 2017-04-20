package net.thepokerguys

import android.content.Intent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import net.thepokerguys.util.hasAllPermissions


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
