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
            viewModel.selectedGenres.clear()
            viewModel.selectedCategory = null
            viewModel.selectedStatusId = null
            viewModel.selectedAgeRating = null
            viewModel.selectedStartYear = null
            viewModel.selectedEndYear = null

            binding.spinnerCategory.text = "Неважно"
            binding.spinnerStatus.text = "Неважно"
            binding.spinnerAge.text = "Неважно"
            binding.spinnerYear.text = "Неважно"
            updateGenreSpinnerText()
        }

        binding.applyButton.setOnClickListener {
            val result = Bundle().apply {
                putStringArrayList("selectedGenres", ArrayList(viewModel.selectedGenres))
                putString("selectedCategory", viewModel.selectedCategory)
                putInt("selectedStatusId", viewModel.selectedStatusId ?: -1)
                putString("selectedAgeRating", viewModel.selectedAgeRating)
                putInt("selectedStartYear", viewModel.selectedStartYear ?: -1)
                putInt("selectedEndYear", viewModel.selectedEndYear ?: -1)
            }

            parentFragmentManager.setFragmentResult("filterRequest", result)
            findNavController().navigateUp()
        }

        val database = AppDatabase.getDatabase(requireContext())
        viewModel = ViewModelProvider(
            this,
            FilterViewModelFactory(
                database.genreDao(),
                database.categoryDao(),
                database.contentDao(),
                database.statusTypeDao()
            )
        )[FilterViewModel::class.java]

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = mutableListOf("Неважно") + categories.map { it.name }
            binding.spinnerCategory.setItems(categoryNames)

            val selected = viewModel.selectedCategory
            binding.spinnerCategory.text = selected ?: "Неважно"

            binding.spinnerCategory.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerCategory.text = "Неважно"
                    viewModel.selectedCategory = null
                } else {
                    binding.spinnerCategory.text = item
                    viewModel.selectedCategory = item
                }
            }
        }

        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            updateGenreSpinnerText()
            binding.spinnerGenre.setOnClickListener {
                GenreFilterDialog(
                    genres = genres,
                    selectedGenres = viewModel.selectedGenres
                ) { selected ->
                    viewModel.selectedGenres = selected.toMutableSet()
                    updateGenreSpinnerText()
                }.show(parentFragmentManager, "GenreFilterDialog")
            }
        }

        viewModel.getYearRange().observe(viewLifecycleOwner) { yearRange ->
            val (minYear, maxYear) = yearRange

            if (viewModel.selectedStartYear != null && viewModel.selectedEndYear != null) {
                binding.spinnerYear.text = getString(
                    R.string.year_range,
                    viewModel.selectedStartYear.toString(),
                    viewModel.selectedEndYear.toString()
                )
            } else {
                binding.spinnerYear.text = "Неважно"
            }

            binding.spinnerYear.setOnClickListener {
                YearFilterDialog({ startYear, endYear ->
                    if (startYear == null && endYear == null) {
                        binding.spinnerYear.text = "Неважно"
                        viewModel.selectedStartYear = null
                        viewModel.selectedEndYear = null
                        return@YearFilterDialog
                    }

                    if (startYear != null && endYear != null) {
                        binding.spinnerYear.text = getString(
                            R.string.year_range,
                            startYear.toString(),
                            endYear.toString()
                        )
                        viewModel.selectedStartYear = startYear
                        viewModel.selectedEndYear = endYear
                    }
                }, minYear, maxYear)
                    .show(parentFragmentManager, "yearFilterDialog")
            }
        }

        viewModel.ageRatings.observe(viewLifecycleOwner) { ageRatings ->
            val ageRatingList = mutableListOf("Неважно") + ageRatings
            binding.spinnerAge.setItems(ageRatingList)

            val selected = viewModel.selectedAgeRating
            binding.spinnerAge.text = selected ?: "Неважно"

            binding.spinnerAge.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerAge.text = "Неважно"
                    viewModel.selectedAgeRating = null
                } else {
                    binding.spinnerAge.text = item
                    viewModel.selectedAgeRating = item
                }
            }
        }

        viewModel.statusTypes.observe(viewLifecycleOwner) { statusTypes ->
            val statusNames = mutableListOf("Неважно") + statusTypes.map { it.name }
            binding.spinnerStatus.setItems(statusNames)

            val selectedId = viewModel.selectedStatusId
            val selectedStatus = statusTypes.find { it.id == selectedId }?.name
            binding.spinnerStatus.text = selectedStatus ?: "Неважно"

            binding.spinnerStatus.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerStatus.text = "Неважно"
                    viewModel.selectedStatusId = null
                } else {
                    binding.spinnerStatus.text = item
                    viewModel.selectedStatusId = statusTypes[position - 1].id
                }
            }
        }
    }

    private fun updateGenreSpinnerText() {
        if (viewModel.selectedGenres.isEmpty()) {
            binding.spinnerGenre.text = "Неважно"
        } else {
            binding.spinnerGenre.text = viewModel.selectedGenres.joinToString(", ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}