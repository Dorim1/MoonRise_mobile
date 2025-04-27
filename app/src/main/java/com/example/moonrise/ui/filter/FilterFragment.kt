package com.example.moonrise.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.R
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.databinding.FragmentFilterBinding


class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private var selectedGenres = mutableSetOf<String>()

    private lateinit var viewModel: FilterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val genreDao = database.genreDao()
        val categoryDao = database.categoryDao()
        val contentDao = database.contentDao()

        viewModel = ViewModelProvider(this, FilterViewModelFactory(genreDao, categoryDao, contentDao))[FilterViewModel::class.java]

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            binding.spinnerCategory.setItems(categoryNames)
        }

        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            binding.spinnerGenre.setOnClickListener {
                GenreFilterDialog(
                    genres = genres,
                    selectedGenres = selectedGenres
                ) { selected ->
                    selectedGenres = selected.toMutableSet()
                    updateGenreSpinnerText()
                }.show(parentFragmentManager, "GenreFilterDialog")
            }
        }

        viewModel.getYearRange().observe(viewLifecycleOwner) { yearRange ->
            val (minYear, maxYear) = yearRange
            binding.spinnerYear.setOnClickListener {
                YearFilterDialog({ startYear, endYear ->
                    // Обрабатываем выбранные годы
                    if (startYear != null && endYear != null) {
                        binding.spinnerYear.text = getString(R.string.year_range, startYear.toString(), endYear.toString())
                    } else {
                        // Когда тексты "С" и "ПО" используются по умолчанию
                        binding.spinnerYear.text = getString(R.string.year_range, startYear?.toString() ?: "С", endYear?.toString() ?: "ПО")
                    }
                }, minYear, maxYear, defaultStartText = "С", defaultEndText = "ПО")
                    .show(parentFragmentManager, "yearFilterDialog")
            }
        }
    }

    private fun updateGenreSpinnerText() {
        if (selectedGenres.isEmpty()) {
            binding.spinnerGenre.text = "Неважно"
        } else {
            binding.spinnerGenre.text = selectedGenres.joinToString(", ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}