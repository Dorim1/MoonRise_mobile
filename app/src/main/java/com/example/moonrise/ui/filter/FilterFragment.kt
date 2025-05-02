package com.example.moonrise.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moonrise.R
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.databinding.FragmentFilterBinding


class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private var selectedGenres = mutableSetOf<String>()
    private var selectedStatusId: Int? = null
    private var selectedCategory: String? = null
    private var selectedAgeRating: String? = null
    private var selectedStartYear: Int? = null
    private var selectedEndYear: Int? = null

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

        val backButton = view.findViewById<AppCompatImageButton>(R.id.back_button)
        val cancelButton = view.findViewById<AppCompatButton>(R.id.cancel_button)

        backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        cancelButton.setOnClickListener {
            selectedGenres.clear()
            selectedCategory = null
            selectedStatusId = null
            selectedAgeRating = null
            selectedStartYear = null
            selectedEndYear = null

            binding.spinnerCategory.text = "Неважно"
            binding.spinnerStatus.text = "Неважно"
            binding.spinnerAge.text = "Неважно"
            binding.spinnerYear.text = "Неважно"
            updateGenreSpinnerText()
        }

        binding.applyButton.setOnClickListener {
            val result = Bundle().apply {
                putStringArrayList("selectedGenres", ArrayList(selectedGenres))
                putString("selectedCategory", selectedCategory)
                putInt("selectedStatusId", selectedStatusId ?: -1)
                putString("selectedAgeRating", selectedAgeRating)
                putInt("selectedStartYear", selectedStartYear ?: -1)
                putInt("selectedEndYear", selectedEndYear ?: -1)
            }

            parentFragmentManager.setFragmentResult("filterRequest", result)
            findNavController().navigateUp()
        }


        val database = AppDatabase.getDatabase(requireContext())
        val genreDao = database.genreDao()
        val categoryDao = database.categoryDao()
        val contentDao = database.contentDao()
        val statusTypeDao = database.statusTypeDao()

        viewModel = ViewModelProvider(
            this,
            FilterViewModelFactory(genreDao, categoryDao, contentDao, statusTypeDao)
        )[FilterViewModel::class.java]

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = mutableListOf("Неважно") + categories.map { it.name }
            binding.spinnerCategory.setItems(categoryNames)

            binding.spinnerCategory.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerCategory.text = "Неважно"
                    selectedCategory = null // Обнуляем выбранную категорию
                } else {
                    binding.spinnerCategory.text = item
                    selectedCategory = item // Сохраняем выбранную категорию
                }
            }
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
                    if (startYear == null && endYear == null) {
                        binding.spinnerYear.text = "Неважно"
                        selectedStartYear = null
                        selectedEndYear = null
                        return@YearFilterDialog
                    }

                    if (startYear != null && endYear != null) {
                        binding.spinnerYear.text = getString(R.string.year_range, startYear.toString(), endYear.toString())
                        selectedStartYear = startYear
                        selectedEndYear = endYear
                    }
                }, minYear, maxYear)
                    .show(parentFragmentManager, "yearFilterDialog")
            }
        }

        viewModel.ageRatings.observe(viewLifecycleOwner) { ageRatings ->
            val ageRatingList = mutableListOf("Неважно") + ageRatings
            binding.spinnerAge.setItems(ageRatingList)

            binding.spinnerAge.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerAge.text = "Неважно"
                    selectedAgeRating = null // Сброс ограничения
                } else {
                    binding.spinnerAge.text = item
                    selectedAgeRating = item // Сохранение ограничения
                }
            }
        }

        viewModel.statusTypes.observe(viewLifecycleOwner) { statusTypes ->
            val statusNames = mutableListOf("Неважно") + statusTypes.map { it.name }
            binding.spinnerStatus.setItems(statusNames)

            binding.spinnerStatus.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerStatus.text = "Неважно"
                    selectedStatusId = null
                } else {
                    binding.spinnerStatus.text = item
                    selectedStatusId = statusTypes[position - 1].id
                }
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