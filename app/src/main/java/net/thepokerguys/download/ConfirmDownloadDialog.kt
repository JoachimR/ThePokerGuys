package net.thepokerguys.download

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils.isEmpty
import net.thepokerguys.R
import net.thepokerguys.eventbus.AppEventBus
import net.thepokerguys.eventbus.DownloadConfirmed

class ConfirmDownloadDialog : DialogFragment() {

    companion object {

        private val KEY_URL = "KEY_URL"
        private val KEY_FILE_NAME = "KEY_FILE_NAME"

        fun createInstance(url: String, fileName: String): ConfirmDownloadDialog {
            val args = Bundle()
            val dialogFragment = ConfirmDownloadDialog()
            args.putString(KEY_URL, url)
            args.putString(KEY_FILE_NAME, fileName)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    lateinit var url: String
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.url = arguments.getString(KEY_URL)
        this.name = arguments.getString(KEY_FILE_NAME)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity.let {
            return AlertDialog.Builder(it)
                    .setTitle(R.string.confirm_download_dialog_title)
                    .setMessage(R.string.confirm_download_dialog_message)
                    .setNegativeButton(R.string.confirm_download_dialog_option_cancel, null)
                    .setPositiveButton(R.string.confirm_download_dialog_option_download, { textId, listener ->
                        reportDownloadConfirmed()
                        dismiss()
                    })
                    .create()
        }
    }

    private fun reportDownloadConfirmed() {
        if (!isEmpty(url)) {
            Handler().postDelayed({
                AppEventBus.post(DownloadConfirmed(url))
            }, 300)
        }
    }

}