package com.example.moonrise.ui.item

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.moonrise.R
import com.example.moonrise.data.local.dao.StatusDao
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.Status
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.moonrise.databinding.FragmentAddToGroupBinding
import kotlinx.coroutines.launch

class AddToGroupBottomSheet(
    private val contentId: Int,
    private val currentStatus: String?
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddToGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToGroupBinding.inflate(inflater, container, false)

        val statusDao = AppDatabase.getDatabase(requireContext()).statusDao()

        when (currentStatus) {
            "Смотрю" -> binding.radioWatching.isChecked = true
            "В планах" -> binding.radioPlanned.isChecked = true
            "Просмотрено" -> binding.radioWatched.isChecked = true
            "Отложено" -> binding.radioPaused.isChecked = true
            "Брошено" -> binding.radioDropped.isChecked = true
            "Запланировано" -> binding.radioScheduled.isChecked = true
            else -> binding.radioNotWatching.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            val newStatus = when (checkedId) {
                binding.radioNotWatching.id -> null // удалить
                binding.radioWatching.id -> "Смотрю"
                binding.radioPlanned.id -> "В планах"
                binding.radioWatched.id -> "Просмотрено"
                binding.radioPaused.id -> "Отложено"
                binding.radioDropped.id -> "Брошено"
                binding.radioScheduled.id -> "Запланировано"
                else -> return@setOnCheckedChangeListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                if (newStatus == null) {
                    statusDao.removeStatus(contentId)
                    Toast.makeText(requireContext(), "Удалено из группы", Toast.LENGTH_SHORT).show()
                } else {
                    statusDao.insertStatus(Status(contentId, newStatus))
                    Toast.makeText(requireContext(), "Добавлено: $newStatus", Toast.LENGTH_SHORT).show()
                }
                dismiss()
            }
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}