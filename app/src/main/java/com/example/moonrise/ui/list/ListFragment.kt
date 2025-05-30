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

        setupRecyclerView(view)
        setupSearchView(view)

        viewModel.filteredContent.observe(viewLifecycleOwner) { contentList ->
            contentAdapter.setContentList(contentList)
        }

        viewModel.checkAndInitDatabase(requireContext()) {}

        viewModel.refreshContent()

        view.findViewById<View>(R.id.filter_button).setOnClickListener {
            navController.navigate(R.id.action_navigation_list_to_navigation_filter)
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.itemsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = contentAdapter
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.search_in_list)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchContent(it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchContent(newText.orEmpty())
                return true
            }
        })
    }
}