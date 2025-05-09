package com.example.moonrise.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contentAdapter: ContentAdapter
    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        contentAdapter = ContentAdapter(navController)

        recyclerView = view.findViewById(R.id.itemsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contentAdapter

        val searchView = view.findViewById<SearchView>(R.id.search_in_list)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchContent(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchContent(newText.orEmpty())
                return true
            }
        })

        viewModel.filteredContent.observe(viewLifecycleOwner) { contentList ->
            contentAdapter.setContentList(contentList)

            // Если контент пустой, загружаем данные
            if (contentList.isEmpty()) {
                viewModel.loadDataFromJson(requireContext()) // Загрузка данных из JSON
            }
        }

        viewModel.loadAllContent()
        viewModel.loadCategoriesFromJson(requireContext())
        viewModel.loadGenresFromJson(requireContext())
        viewModel.loadContentGenresFromJson(requireContext())
        viewModel.loadStatusTypesFromJson(requireContext())

        val filterButton = view.findViewById<View>(R.id.filter_button)
        filterButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_list_to_navigation_filter)
        }

    }
}