package com.example.moonrise.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        viewModel.allContentWithCategory.observe(viewLifecycleOwner) { contentList ->
            contentAdapter.setContentList(contentList)

            if (contentList.isEmpty()) {
                viewModel.loadDataFromJson(requireContext())  // Загрузка данных контента из JSON
            }
        }

        // Вызов функции для загрузки категорий, если таблица пустая
        viewModel.loadCategoriesFromJson(requireContext())
    }
}