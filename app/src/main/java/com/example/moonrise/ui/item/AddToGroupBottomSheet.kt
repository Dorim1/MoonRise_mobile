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
    private val currentStatusId: Int? // теперь приходит id статуса, а не текст
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddToGroupBinding? = null
    private val binding get() = _binding!!

    // Радиокнопки и их соответствующие statusTypeId
    private val statusIdMap by lazy {
        mapOf(
            R.id.radio_not_watching to null,
            R.id.radio_watching to 1,   // "Смотрю"
            R.id.radio_planned to 2,    // "В планах"
            R.id.radio_watched to 3,    // "Просмотрено"
            R.id.radio_favorite to 4,   // "Любимое"
            R.id.radio_paused to 5,     // "Отложено"
            R.id.radio_dropped to 6   // "Брошено"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToGroupBinding.inflate(inflater, container, false)

        val statusDao = AppDatabase.getDatabase(requireContext()).statusDao()
        val statusTypeDao = AppDatabase.getDatabase(requireContext()).statusTypeDao()

        // Установить выбранную радиокнопку по текущему статусу
        val selectedRadioId = statusIdMap.entries.find { it.value == currentStatusId }?.key
        selectedRadioId?.let { binding.radioGroup.check(it) }

        binding.radioGroup.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
            val newStatusId = statusIdMap[checkedId]

            viewLifecycleOwner.lifecycleScope.launch {
                if (newStatusId == null) {
                    statusDao.removeStatus(contentId)
                    Toast.makeText(requireContext(), "Статус удалён", Toast.LENGTH_SHORT).show()
                } else {
                    statusDao.insertStatus(Status(contentId, newStatusId))
                    val statusType = statusTypeDao.getStatusTypeById(newStatusId)
                    val statusName = statusType?.name ?: "Неизвестно"
                    Toast.makeText(requireContext(), "Статус: $statusName", Toast.LENGTH_SHORT).show()
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