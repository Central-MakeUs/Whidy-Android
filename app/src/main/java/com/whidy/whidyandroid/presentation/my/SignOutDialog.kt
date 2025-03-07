package com.whidy.whidyandroid.presentation.my

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.DialogSignOutBinding

class SignOutDialog(
    context: Context,
    private val onSignOutClicked: () -> Unit
) : Dialog(context, R.style.CustomDialogDimmed) {
    private lateinit var binding: DialogSignOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSignOutBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupDialogAppearance()
        setupListeners()
    }

    private fun setupDialogAppearance() {
        window?.setLayout(
            context.resources.getDimensionPixelSize(R.dimen.dialog_place_add_width),
            context.resources.getDimensionPixelSize(R.dimen.dialog_sign_out_height)
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupListeners() {
        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
        binding.btnSignOut.setOnClickListener {
            onSignOutClicked.invoke()
            dismiss()
        }
    }
}