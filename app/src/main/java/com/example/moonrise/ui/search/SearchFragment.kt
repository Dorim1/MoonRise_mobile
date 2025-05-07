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

        val imageLeft = view.findViewById<ImageView>(R.id.franchise_image_left)
        val imageRight = view.findViewById<ImageView>(R.id.franchise_image_right)
        val imageCenter = view.findViewById<ImageView>(R.id.franchise_image_center)

        val navController = findNavController()
        adapter = ContentAdapter(navController)
        recyclerView = view.findViewById(R.id.search_result_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.franchiseContent.observe(viewLifecycleOwner) { relatedList ->
            if (relatedList.isNotEmpty()) {
                franchiseBlock.visibility = View.VISIBLE
                franchiseInfo.text = getString(R.string.franchise_releases, relatedList.size)

                // Центр — первый элемент
                Glide.with(this)
                    .load(relatedList.getOrNull(0)?.content?.image)
                    .into(imageCenter)

                // Слева — второй элемент (если есть)
                if (relatedList.size > 1) {
                    imageLeft.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(relatedList.getOrNull(1)?.content?.image)
                        .into(imageLeft)
                } else {
                    imageLeft.visibility = View.GONE
                }

                // Справа — третий элемент (если есть)
                if (relatedList.size > 2) {
                    imageRight.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(relatedList.getOrNull(2)?.content?.image)
                        .into(imageRight)
                } else {
                    imageRight.visibility = View.GONE
                }

                // Кнопка "Перейти" (если нужна)
                // view.findViewById<AppCompatButton>(R.id.franchise_button).setOnClickListener { ... }

            } else {
                franchiseBlock.visibility = View.GONE
            }
        }

        // Наблюдение за результатами поиска
        viewModel.filteredContent.observe(viewLifecycleOwner) { results ->
            adapter.setContentList(results)
            val resultsBlock = view.findViewById<View>(R.id.search_results_block)
            resultsBlock.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }

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


    }
}