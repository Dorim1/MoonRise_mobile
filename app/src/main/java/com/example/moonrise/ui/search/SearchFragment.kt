package com.example.moonrise.ui.search

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moonrise.R
import com.example.moonrise.ui.list.ContentAdapter

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContentAdapter
    private val viewModel: SearchViewModel by viewModels()
    private var isFilterApplied = false
    var currentQuery = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val franchiseBlock = view.findViewById<View>(R.id.franchise_block)
        val franchiseInfo = view.findViewById<TextView>(R.id.franchise_info)

        val searchView = view.findViewById<SearchView>(R.id.search_in_list)
        val historyRecycler = view.findViewById<RecyclerView>(R.id.search_history_recycler)
        val historyAdapter = SearchHistoryAdapter(
            onItemClick = { selectedQuery ->
                searchView.setQuery(selectedQuery, true)
            },
            onDeleteClick = { queryToRemove ->
                viewModel.removeQueryFromHistory(queryToRemove)
            }
        )

        // Скрываем историю по умолчанию
        historyRecycler.visibility = View.GONE
        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = historyAdapter

        val navController = findNavController()
        adapter = ContentAdapter(navController)
        recyclerView = view.findViewById(R.id.search_result_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.franchiseContent.observe(viewLifecycleOwner) { relatedList ->
            if (relatedList.isNotEmpty()) {
                franchiseBlock.visibility = View.VISIBLE
                franchiseInfo.text = getString(R.string.franchise_releases, relatedList.size)
                // Загрузка изображений...
            } else {
                franchiseBlock.visibility = View.GONE
            }
        }

        parentFragmentManager.setFragmentResultListener("filterRequest", viewLifecycleOwner) { _, bundle ->
            isFilterApplied = true
            historyRecycler.visibility = View.GONE
            val selectedGenres = bundle.getStringArrayList("selectedGenres") ?: emptyList()
            val selectedCategory = bundle.getString("selectedCategory")
            val selectedStatusId = bundle.getInt("selectedStatusId").takeIf { it != -1 }
            val selectedAgeRating = bundle.getString("selectedAgeRating")
            val selectedStartYear = bundle.getInt("selectedStartYear").takeIf { it != -1 }
            val selectedEndYear = bundle.getInt("selectedEndYear").takeIf { it != -1 }

            viewModel.applyFilters(
                genres = selectedGenres,
                category = selectedCategory,
                statusId = selectedStatusId,
                ageRating = selectedAgeRating,
                startYear = selectedStartYear,
                endYear = selectedEndYear
            )
        }

        viewModel.initManager(requireContext())

        viewModel.filteredContent.observe(viewLifecycleOwner) { results ->
            adapter.setContentList(results)
            val resultsBlock = view.findViewById<View>(R.id.search_results_block)

            val shouldShowResults = (currentQuery.isNotBlank() || isFilterApplied) && results.isNotEmpty()
            resultsBlock.visibility = if (shouldShowResults) View.VISIBLE else View.GONE
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val isQueryEmpty = currentQuery.isBlank()
            val hasHistory = viewModel.searchHistory.value?.isNotEmpty() == true
            historyRecycler.visibility = if (hasFocus && isQueryEmpty && !isFilterApplied && hasHistory) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Слушатель для ввода текста
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                viewModel.addQueryToHistory(currentQuery)
                viewModel.searchContent(currentQuery)
                historyRecycler.visibility = View.GONE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                viewModel.searchContent(currentQuery)
                historyRecycler.visibility = View.GONE // не показываем при вводе
                return true
            }
        })

        // Наблюдение за историей поиска
        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.setHistory(history)
        }

        val filterButton = view.findViewById<View>(R.id.filter_button)
        filterButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_search_to_navigation_filter)
        }
    }
}