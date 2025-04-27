package com.example.moonrise.ui.filter

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.moonrise.R
import com.example.moonrise.data.local.entity.Genre

class GenreFilterDialog(
    private val genres: List<Genre>,
    private val selectedGenres: MutableSet<String>,
    private val onGenresSelected: (Set<String>) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filter_genres, container, false)

        val checkboxContainer = view.findViewById<LinearLayout>(R.id.checkbox_container)
        val btnSelect = view.findViewById<AppCompatButton>(R.id.btn_select)
        val btnReset = view.findViewById<AppCompatButton>(R.id.btn_reset)

        genres.forEach { genre ->
            val checkBox = CheckBox(requireContext()).apply {
                text = genre.name
                isChecked = selectedGenres.contains(genre.name)
                buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.checkbox_color))
            }
            checkboxContainer.addView(checkBox)
        }

        btnSelect.setOnClickListener {
            val newSelectedGenres = mutableSetOf<String>()
            for (i in 0 until checkboxContainer.childCount) {
                val checkBox = checkboxContainer.getChildAt(i) as CheckBox
                if (checkBox.isChecked) {
                    newSelectedGenres.add(checkBox.text.toString())
                }
            }
            onGenresSelected(newSelectedGenres)
            dismiss()
        }

        btnReset.setOnClickListener {
            selectedGenres.clear()
            onGenresSelected(emptySet())
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // Устанавливаем размеры окна (90% ширины экрана)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}