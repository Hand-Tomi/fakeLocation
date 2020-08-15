package com.sugaryple.fakelocation.maps

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sugaryple.fakelocation.R
import kotlinx.android.synthetic.main.alert_dialog_image_view.view.*

class RequiredMockLocationDialog: DialogFragment() {

    companion object {
        fun newInstance(): DialogFragment = RequiredMockLocationDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setView(
                View.inflate(requireContext(), R.layout.alert_dialog_image_view, null).apply {
                    this.image_view.setImageResource(R.drawable.sample_image)
                }
            )
            .setTitle(R.string.request_selecting_mock_location_dialog_title)
            .setMessage(R.string.request_selecting_mock_location_dialog_description)
            .setPositiveButton(
                R.string.request_selecting_mock_location_dialog_button_debug_setting
            ) { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            }
            .setNegativeButton(
                R.string.request_selecting_mock_location_dialog_button_cancel
            ) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }
}