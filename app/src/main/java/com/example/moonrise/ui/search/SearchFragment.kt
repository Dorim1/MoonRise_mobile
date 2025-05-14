package com.example.moonrise.ui.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moonrise.R
import com.example.moonrise.ui.list.ContentAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContentAdapter
    private val viewModel: SearchViewModel by viewModels()
    private var isFilterApplied = false
    private var searchJob: Job? = null

    private val voiceInputLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                val searchView = view?.findViewById<SearchView>(R.id.search_in_list)
                searchView?.setQuery(spokenText, true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val franchiseBlock = view.findViewById<View>(R.id.franchise_block)
        val franchiseInfo = view.findViewById<TextView>(R.id.franchise_info)
        val searchView = view.findViewById<SearchView>(R.id.search_in_list)
        val historyRecycler = view.findViewById<RecyclerView>(R.id.search_history_recycler)
        val franchiseButton = view.findViewById<AppCompatButton>(R.id.franchise_button)
        val franchiseCloseButton = view.findViewById<AppCompatImageButton>(R.id.franchise_close_button)
        val franchiseContentBlock = view.findViewById<View>(R.id.franchise_content_block)

        val imageCenter = view.findViewById<ImageView>(R.id.franchise_image_center)
        val imageLeft = view.findViewById<ImageView>(R.id.franchise_image_left)
        val imageRight = view.findViewById<ImageView>(R.id.franchise_image_right)

        val historyAdapter = SearchHistoryAdapter(
            onItemClick = { selectedQuery -> searchView.setQuery(selectedQuery, true) },
            onDeleteClick = { queryToRemove -> viewModel.removeQueryFromHistory(queryToRemove) }
        )

        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = historyAdapter

        adapter = ContentAdapter(findNavController())
        recyclerView = view.findViewById(R.id.search_result_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.initManager(requireContext())

        viewModel.franchiseContent.observe(viewLifecycleOwner) { relatedList ->
            franchiseBlock.visibility = if (relatedList.isNotEmpty()) View.VISIBLE else View.GONE
            franchiseInfo.text = getString(R.string.franchise_releases, relatedList.size)

            if (relatedList.isNotEmpty()) {
                // Загружаем первое изображение в центр
                Glide.with(this)
                    .load(relatedList.getOrNull(0)?.content?.image)
                    .into(imageCenter)

                // Второе — слева
                Glide.with(this)
                    .load(relatedList.getOrNull(1)?.content?.image)
                    .into(imageLeft)

                // Третье — справа
                Glide.with(this)
                    .load(relatedList.getOrNull(2)?.content?.image)
                    .into(imageRight)
            } else {
                imageCenter.setImageDrawable(null)
                imageLeft.setImageDrawable(null)
                imageRight.setImageDrawable(null)
            }
        }

        viewModel.filteredContent.observe(viewLifecycleOwner) { results ->
            adapter.setContentList(results)
            val resultsBlock = view.findViewById<View>(R.id.search_results_block)
            val shouldShowResults = (viewModel.currentQuery.isNotBlank() || isFilterApplied) && results.isNotEmpty()
            resultsBlock.visibility = if (shouldShowResults) View.VISIBLE else View.GONE
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val isQueryEmpty = viewModel.currentQuery.isBlank()
            val hasHistory = viewModel.searchHistory.value?.isNotEmpty() == true
            historyRecycler.visibility = if (hasFocus && isQueryEmpty && !isFilterApplied && hasHistory) View.VISIBLE else View.GONE
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val cleanQuery = query.orEmpty()
                viewModel.setCurrentQuery(cleanQuery)
                viewModel.addQueryToHistory(cleanQuery)
                viewModel.searchContent(cleanQuery)
                historyRecycler.visibility = View.GONE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cleanQuery = newText.orEmpty()
                viewModel.setCurrentQuery(cleanQuery)

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(300)
                    viewModel.searchContent(cleanQuery)
                }

                historyRecycler.visibility = View.GONE
                return true
            }
        })

        viewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.setHistory(history)
        }

        val filterButton = view.findViewById<View>(R.id.filter_button)
        filterButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_search_to_navigation_filter)
        }

        franchiseCloseButton.setOnClickListener {
            val isCurrentlyVisible = franchiseContentBlock.isVisible
            franchiseContentBlock.isVisible = !isCurrentlyVisible

            if (isCurrentlyVisible) {
                franchiseCloseButton.setImageResource(R.drawable.ic_eye_closed)
            } else {
                franchiseCloseButton.setImageResource(R.drawable.ic_eye_open)
            }
        }

        val voiceButton = view.findViewById<View>(R.id.voice_button)
        voiceButton.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите запрос для поиска")
            }

            try {
                voiceInputLauncher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Голосовой ввод не поддерживается", Toast.LENGTH_SHORT).show()
            }
        }

        franchiseButton.setOnClickListener {
            val franchiseId = viewModel.getLastFranchiseContentId()
            if (franchiseId != null) {
                val bundle = Bundle().apply {
                    putInt("contentId", franchiseId)
                }
                findNavController().navigate(R.id.action_navigation_search_to_franchiseFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Франшиза не найдена", Toast.LENGTH_SHORT).show()
            }
        }
    }
}