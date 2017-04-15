package de.reiss.thepokerguys

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.util.Linkify
import android.widget.TextView

class AppInfoDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.app_name))
                .setMessage((activity.application as App).appInfo)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.ok, null)
                .create()
    }

    override fun onStart() {
        super.onStart()
        Linkify.addLinks(dialog.findViewById(android.R.id.message) as TextView, Linkify.ALL)
    }

}