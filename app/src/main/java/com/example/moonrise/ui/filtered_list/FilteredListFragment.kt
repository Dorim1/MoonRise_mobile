package com.example.moonrise.ui.filtered_list

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
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

        val emptyView = view.findViewById<TextView>(R.id.emptyView)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        contentAdapter = ContentAdapter(navController)

        recyclerView = view.findViewById(R.id.filteredItemsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contentAdapter
        recyclerView.setHasFixedSize(true)

        val args = arguments
        val selectedGenres = args?.getStringArrayList("selectedGenres") ?: emptyList()
        val selectedCategory = args?.getString("selectedCategory")
        val selectedStatusId = args?.getInt("selectedStatusId")?.takeIf { it != -1 }
        val selectedAgeRating = args?.getString("selectedAgeRating")
        val selectedStartYear = args?.getInt("selectedStartYear")?.takeIf { it != -1 }
        val selectedEndYear = args?.getInt("selectedEndYear")?.takeIf { it != -1 }
        val selectedSort = args?.getString("selectedSort")

        viewModel.applyFilters(
            selectedGenres,
            selectedCategory,
            selectedStatusId,
            selectedAgeRating,
            selectedStartYear,
            selectedEndYear
        )

        viewModel.filteredContent.observe(viewLifecycleOwner) { filteredList ->

            val sortedList = when (selectedSort) {
                "По названию (А–Я)" -> filteredList.sortedBy { it.content.title }
                "По названию (Я–А)" -> filteredList.sortedByDescending { it.content.title }
                "По дате (Сначала новые)" -> filteredList.sortedByDescending { it.content.releaseDate }
                "По дате (Сначала старые)" -> filteredList.sortedBy { it.content.releaseDate }
                else -> filteredList
            }

            contentAdapter.setContentList(sortedList)

            if (sortedList.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        val navigateUpListener = View.OnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<AppCompatImageButton>(R.id.back_button).setOnClickListener(navigateUpListener)
        view.findViewById<AppCompatButton>(R.id.editFiltersButton).setOnClickListener(navigateUpListener)
    }
}