package com.example.moonrise.ui.item

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.moonrise.databinding.BottomSheetCopyTitlesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CopyTitlesBottomSheet(
    private val title: String,
    private val orTitle: String
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCopyTitlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCopyTitlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fullTitle.text = title
        binding.fullOrTitle.text = orTitle

        binding.copyTitleButton.setOnClickListener {
            copyToClipboard("Название", title)
        }

        binding.copyOrTitleButton.setOnClickListener {
            copyToClipboard("Оригинальное название", orTitle)
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(binding.root.context, "$label скопировано", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}