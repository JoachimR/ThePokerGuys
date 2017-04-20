package net.thepokerguys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import net.thepokerguys.util.hasFilePermission

class AskForPermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_activity)
        askUserToActivateNextCapabilityOrFinish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        askUserToActivateNextCapabilityOrFinish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        askUserToActivateNextCapabilityOrFinish()
    }

    private fun askUserToActivateNextCapabilityOrFinish() {
        if (!hasFilePermission(this)) {
            showPermissionDialog()
        } else {
            finishOk()
        }
    }

    private fun showPermissionDialog(): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            return true
        }
        return false
    }

    private fun finishOk() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}
