package com.example.moonrise.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.StatusWithCount
import com.example.moonrise.databinding.FragmentProfileBinding
import com.example.moonrise.ui.list.ContentAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val statusDao = db.statusDao()
        val statusTypeDao = db.statusTypeDao()
        val contentDao = db.contentDao()

        val viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val searchView = binding.searchProfile
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchItems(it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchItems(newText.orEmpty())
                return true
            }
        })

        val filterRecycler = binding.recyclerFilter
        filterRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val navController = findNavController()
        val contentAdapter = ContentAdapter(navController)
        val itemsRecycler = binding.itemsList
        itemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        itemsRecycler.adapter = contentAdapter

        lifecycleScope.launch {
            val statusTypes = statusTypeDao.getAllStatusTypes()
            val statusNameMap = statusTypes.associateBy({ it.id }, { it.name })

            val statuses = statusDao.getAllStatuses().first()
            val allItems = contentDao.getAllContentWithCategory().first()
            viewModel.setAllItems(allItems, statusNameMap)

            val statusCountMap = statuses.groupingBy { it.statusTypeId }.eachCount()
            val filterItems = statusTypes.map {
                StatusWithCount(
                    name = it.name,
                    count = statusCountMap[it.id] ?: 0
                )
            }.toMutableList()

            val totalCount = statuses.size
            filterItems.add(0, StatusWithCount("Все", totalCount))

            filterRecycler.adapter = FilterAdapter(filterItems) { selected ->
                viewModel.filterByStatusName(selected.name)
            }
            viewModel.filterByStatusName("Все")
        }
        // Отображение надписи при пустом списке
        viewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.emptyMessage.visibility = View.VISIBLE
            } else {
                binding.emptyMessage.visibility = View.GONE
            }

            contentAdapter.setContentList(items)
        }

        viewModel.filteredItems.observe(viewLifecycleOwner) { filteredList ->
            contentAdapter.setContentList(filteredList)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}