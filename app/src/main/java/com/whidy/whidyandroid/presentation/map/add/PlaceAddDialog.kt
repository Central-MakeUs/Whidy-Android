package com.whidy.whidyandroid.presentation.map.add

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.whidy.whidyandroid.R
import com.whidy.whidyandroid.databinding.DialogPlaceAddRequestCompleteBinding

class PlaceAddDialog(
    context: Context,
) : Dialog(context, R.style.CustomDialogDimmed) {
    private lateinit var binding: DialogPlaceAddRequestCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPlaceAddRequestCompleteBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        setupDialogAppearance()
        setupListeners()
    }

    private fun setupDialogAppearance() {
        window?.setLayout(
            context.resources.getDimensionPixelSize(R.dimen.dialog_place_add_width),
            context.resources.getDimensionPixelSize(R.dimen.dialog_place_add_height)
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupListeners() {
        binding.btnConfirm.setOnClickListener {
            dismiss()
        }
    }
}