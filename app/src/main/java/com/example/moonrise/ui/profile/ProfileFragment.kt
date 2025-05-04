package com.example.moonrise.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.StatusWithCount
import com.example.moonrise.databinding.FragmentProfileBinding
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
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val statusDao = db.statusDao()
        val statusTypeDao = db.statusTypeDao()

        val recycler = binding.recyclerFilter
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            val statusTypes = statusTypeDao.getAllStatusTypes()
            val statuses = statusDao.getAllStatuses().first()

            val statusCountMap = statuses.groupingBy { it.statusTypeId }.eachCount()
            val filterItems = statusTypes.map {
                StatusWithCount(
                    name = it.name,
                    count = statusCountMap[it.id] ?: 0
                )
            }.toMutableList()

            val totalCount = statuses.size
            filterItems.add(0, StatusWithCount("Все", totalCount))

            recycler.adapter = FilterAdapter(filterItems) { selected ->
                // TODO: отфильтровать itemsList по выбранному статусу
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}