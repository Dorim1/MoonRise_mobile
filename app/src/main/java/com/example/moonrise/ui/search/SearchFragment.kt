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

        viewModel.initManager(requireContext())

        viewModel.filteredContent.observe(viewLifecycleOwner) { results ->
            adapter.setContentList(results)
            val resultsBlock = view.findViewById<View>(R.id.search_results_block)
            resultsBlock.visibility = if (currentQuery.isNotBlank() && results.isNotEmpty()) View.VISIBLE else View.GONE
        }

        // Слушатель для изменения фокуса в поле поиска
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Если фокус потерян, скрываем историю
                historyRecycler.visibility = View.GONE
            } else {
                // Если фокус на поле поиска, показываем историю, если запрос пустой
                if (currentQuery.isBlank()) {
                    val history = viewModel.searchHistory.value
                    historyRecycler.visibility = if (history?.isNotEmpty() == true) View.VISIBLE else View.GONE
                } else {
                    historyRecycler.visibility = View.GONE
                }
            }
        }

        // Слушатель для ввода текста
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.addQueryToHistory(currentQuery)
                currentQuery = query.orEmpty()
                viewModel.searchContent(currentQuery)
                historyRecycler.visibility = View.GONE  // Скрыть историю после отправки запроса
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                viewModel.searchContent(currentQuery)

                if (currentQuery.isBlank()) {
                    view.findViewById<View>(R.id.franchise_block).visibility = View.GONE
                    historyRecycler.visibility = if (viewModel.searchHistory.value?.isNotEmpty() == true) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                } else {
                    historyRecycler.visibility = View.GONE // Скрыть историю при вводе текста
                }

                return true
            }
        })

        // Наблюдение за историей поиска
        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.setHistory(history)
        }
    }
}