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

    private val selectedGenres get() = viewModel.selectedGenres

    private var selectedCategory: String?
        get() = viewModel.selectedCategory
        set(value) { viewModel.selectedCategory = value }

    private var selectedStatusId: Int?
        get() = viewModel.selectedStatusId
        set(value) { viewModel.selectedStatusId = value }

    private var selectedAgeRating: String?
        get() = viewModel.selectedAgeRating
        set(value) { viewModel.selectedAgeRating = value }

    private var selectedStartYear: Int?
        get() = viewModel.selectedStartYear
        set(value) { viewModel.selectedStartYear = value }

    private var selectedEndYear: Int?
        get() = viewModel.selectedEndYear
        set(value) { viewModel.selectedEndYear = value }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        val database = AppDatabase.getDatabase(requireContext())
        val genreDao = database.genreDao()
        val categoryDao = database.categoryDao()
        val contentDao = database.contentDao()
        val statusTypeDao = database.statusTypeDao()

        viewModel = ViewModelProvider(
            this,
            FilterViewModelFactory(genreDao, categoryDao, contentDao, statusTypeDao)
        )[FilterViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<AppCompatImageButton>(R.id.back_button)
        val cancelButton = view.findViewById<AppCompatButton>(R.id.cancel_button)

        val sortOptions = listOf(
            "Неважно",
            "По названию (А–Я)",
            "По названию (Я–А)",
            "По дате (Сначала новые)",
            "По дате (Сначала старые)"
        )
        binding.spinnerSort.setItems(sortOptions)

        binding.spinnerSort.post {
            val initialSortIndex = sortOptions.indexOf(viewModel.selectedSort ?: "Неважно")
            if (initialSortIndex >= 0) {
                binding.spinnerSort.selectItemByIndex(initialSortIndex)
            }
        }

        binding.spinnerSort.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
            if (position == 0) {
                viewModel.selectedSort = null
            } else {
                viewModel.selectedSort = item
            }
            binding.spinnerSort.setBackgroundResource(R.drawable.spinner_background)
        }

        binding.spinnerSort.setOnSpinnerDismissListener {
            binding.spinnerSort.setBackgroundResource(R.drawable.spinner_background)
        }

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
                putString("selectedSort", viewModel.selectedSort)
            }

            findNavController().navigate(R.id.action_navigation_filter_to_filteredListFragment, result)
        }


        val database = AppDatabase.getDatabase(requireContext())
        val genreDao = database.genreDao()
        val categoryDao = database.categoryDao()
        val contentDao = database.contentDao()
        val statusTypeDao = database.statusTypeDao()



        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = mutableListOf("Неважно") + categories.map { it.name }
            binding.spinnerCategory.setItems(categoryNames)

            val initialIndex = categoryNames.indexOf(selectedCategory)
            if (binding.spinnerCategory.text == "Неважно" && initialIndex >= 0) {
                binding.spinnerCategory.selectItemByIndex(initialIndex)
                binding.spinnerCategory.text = categoryNames[initialIndex]
            }

            binding.spinnerCategory.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerCategory.text = "Неважно"
                    selectedCategory = null
                } else {
                    binding.spinnerCategory.text = item
                    selectedCategory = item
                }
                binding.spinnerCategory.setBackgroundResource(R.drawable.spinner_background)
            }

            binding.spinnerCategory.setOnSpinnerDismissListener {
                binding.spinnerCategory.setBackgroundResource(R.drawable.spinner_background)
            }
        }
        viewModel.genres.observe(viewLifecycleOwner) { genres ->
            binding.spinnerGenre.setOnClickListener {
                GenreFilterDialog(
                    genres = genres,
                    selectedGenres = selectedGenres
                ) { selected ->
                    selectedGenres.clear()
                    selectedGenres.addAll(selected)
                    updateGenreSpinnerText()
                }.show(parentFragmentManager, "GenreFilterDialog")
            }
        }
        updateGenreSpinnerText()

        if (selectedStartYear != null && selectedEndYear != null) {
            val expected = getString(R.string.year_range, selectedStartYear.toString(), selectedEndYear.toString())
            if (binding.spinnerYear.text == "Неважно") {
                binding.spinnerYear.text = expected
            }
        } else {
            binding.spinnerYear.text = "Неважно"
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

                    binding.spinnerYear.text = getString(
                        R.string.year_range,
                        startYear.toString(),
                        endYear.toString()
                    )
                    selectedStartYear = startYear
                    selectedEndYear = endYear
                }, minYear, maxYear).show(parentFragmentManager, "yearFilterDialog")
            }
        }

        viewModel.ageRatings.observe(viewLifecycleOwner) { ageRatings ->
            val ageRatingList = mutableListOf("Неважно") + ageRatings
            binding.spinnerAge.setItems(ageRatingList)

            val initialAgeIndex = ageRatingList.indexOf(selectedAgeRating)
            if (binding.spinnerAge.text == "Неважно" && initialAgeIndex >= 0) {
                binding.spinnerAge.selectItemByIndex(initialAgeIndex)
                binding.spinnerAge.text = ageRatingList[initialAgeIndex]
            }

            binding.spinnerAge.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerAge.text = "Неважно"
                    selectedAgeRating = null
                } else {
                    binding.spinnerAge.text = item
                    selectedAgeRating = item
                }
                binding.spinnerAge.setBackgroundResource(R.drawable.spinner_background)
            }

            binding.spinnerAge.setOnSpinnerDismissListener {
                binding.spinnerAge.setBackgroundResource(R.drawable.spinner_background)
            }
        }

        viewModel.statusTypes.observe(viewLifecycleOwner) { statusTypes ->
            val statusNames = mutableListOf("Неважно") + statusTypes.map { it.name }
            binding.spinnerStatus.setItems(statusNames)

            val initialStatusIndex = statusTypes.indexOfFirst { it.id == selectedStatusId }
            if (binding.spinnerStatus.text == "Неважно" && initialStatusIndex >= 0) {
                binding.spinnerStatus.selectItemByIndex(initialStatusIndex + 1)
                binding.spinnerStatus.text = statusTypes[initialStatusIndex].name
            }

            binding.spinnerStatus.setOnSpinnerItemSelectedListener<String> { _, _, position, item ->
                if (position == 0) {
                    binding.spinnerStatus.text = "Неважно"
                    selectedStatusId = null
                } else {
                    binding.spinnerStatus.text = item
                    selectedStatusId = statusTypes[position - 1].id
                }
                binding.spinnerStatus.setBackgroundResource(R.drawable.spinner_background)
            }

            binding.spinnerStatus.setOnSpinnerDismissListener {
                binding.spinnerStatus.setBackgroundResource(R.drawable.spinner_background)
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