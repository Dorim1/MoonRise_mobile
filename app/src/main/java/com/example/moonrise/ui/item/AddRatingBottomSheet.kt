package com.example.moonrise.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moonrise.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class AddRatingBottomSheet(
    private val onSaveRating: (Float) -> Unit,
    private val contentId: Int,
    private val viewModel: ItemViewModel
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
        val saveButton = view.findViewById<AppCompatButton>(R.id.save_button)
        val cancelButton = view.findViewById<AppCompatButton>(R.id.cancel_button)

        var originalRating: Float? = null

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRating(contentId).collect { rating ->
                    if (rating != null) {
                        originalRating = rating.ratingValue
                        ratingBar.rating = originalRating!!
                        saveButton.text = getString(R.string.delete_rating)
                    } else {
                        saveButton.text = getString(R.string.save)
                    }
                }
            }
        }

        ratingBar.setOnRatingBarChangeListener { _, newRating, _ ->
            if (originalRating != null && newRating != originalRating) {
                saveButton.text = getString(R.string.change_rating)
            } else if (originalRating == null) {
                saveButton.text = getString(R.string.save)
            } else {
                saveButton.text = getString(R.string.delete_rating)
            }
        }

        saveButton.setOnClickListener {
            val currentRating = ratingBar.rating

            when {
                originalRating != null && currentRating == originalRating -> {
                    viewModel.deleteRating(contentId)
                    onSaveRating(0f)
                }
                else -> {
                    viewModel.saveRating(contentId, currentRating)
                    onSaveRating(currentRating)
                }
            }

            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}