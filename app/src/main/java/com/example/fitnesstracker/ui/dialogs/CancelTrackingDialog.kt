package com.example.fitnesstracker.ui.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.fitnesstracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CancelTrackingDialog(
    private var positiveClick: ((DialogInterface) -> Unit)? = null,
) : DialogFragment() {

    fun setPositiveClickListener(l: (DialogInterface) -> Unit) {
        positiveClick = l
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel_run_title)
            .setMessage(R.string.cancel_run_message)
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton(R.string.yes) { d, _ ->
                positiveClick?.invoke(d)
            }
            .setNegativeButton(R.string.no, null)
            .create()
    }
}