package com.example.moonrise.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonrise.ContentAdapter
import com.example.moonrise.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()
    private lateinit var contentAdapter: ContentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentAdapter = ContentAdapter()
        binding.itemsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contentAdapter
        }

        // Подписка на данные из ViewModel
        viewModel.allContent.observe(viewLifecycleOwner) { contentList ->
            contentAdapter.setContentList(contentList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}