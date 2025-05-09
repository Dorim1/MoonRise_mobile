package com.example.moonrise.ui.FilteredList

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R
import com.example.moonrise.ui.list.ContentAdapter

class FilteredListFragment : Fragment() {

    private val viewModel: FilteredListViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var contentAdapter: ContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_filtered_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentAdapter = ContentAdapter(navController = parentFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!.findNavController())

        recyclerView = view.findViewById(R.id.filteredItemsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contentAdapter

        val selectedGenres = arguments?.getStringArrayList("selectedGenres") ?: emptyList()
        val selectedCategory = arguments?.getString("selectedCategory")
        val selectedStatusId = arguments?.getInt("selectedStatusId")?.takeIf { it != -1 }
        val selectedAgeRating = arguments?.getString("selectedAgeRating")
        val selectedStartYear = arguments?.getInt("selectedStartYear")?.takeIf { it != -1 }
        val selectedEndYear = arguments?.getInt("selectedEndYear")?.takeIf { it != -1 }

        viewModel.applyFilters(
            selectedGenres,
            selectedCategory,
            selectedStatusId,
            selectedAgeRating,
            selectedStartYear,
            selectedEndYear
        )

        viewModel.filteredContent.observe(viewLifecycleOwner) { filteredList ->
            contentAdapter.setContentList(filteredList)
        }
    }
}