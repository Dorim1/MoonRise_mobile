package com.example.moonrise.ui.franchise

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R
import com.example.moonrise.ui.list.ContentAdapter

class FranchiseFragment : Fragment() {

    private lateinit var viewModel: FranchiseViewModel
    private lateinit var adapter: ContentAdapter
    private lateinit var descriptionView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_franchise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentId = requireArguments().getInt("contentId")

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[FranchiseViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.franchiseItemsList)
        descriptionView = view.findViewById(R.id.franchise_info)
        adapter = ContentAdapter(navController = findNavController())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.description.observe(viewLifecycleOwner) { description ->
            descriptionView.text = description
        }

        viewModel.contentList.observe(viewLifecycleOwner) { list ->
            adapter.setContentList(list)
        }

        viewModel.loadFranchise(contentId)

        view.findViewById<AppCompatImageButton>(R.id.back_button).setOnClickListener {
            findNavController().navigateUp()
        }
    }

}