package com.example.moonrise.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatButton
import com.example.moonrise.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddRatingBottomSheet(
    private val onSaveRating: (Float) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_add_rating, container, false)

        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
        val saveButton = view.findViewById<AppCompatButton>(R.id.save_button)
        val cancelButton = view.findViewById<AppCompatButton>(R.id.cancel_button)

        saveButton.setOnClickListener {
            onSaveRating(ratingBar.rating)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}