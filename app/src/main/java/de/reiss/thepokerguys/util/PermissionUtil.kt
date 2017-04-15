package de.reiss.thepokerguys.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

fun hasAllPermissions(context: Context): Boolean {
    return hasFilePermission(context)
}

fun hasFilePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}
