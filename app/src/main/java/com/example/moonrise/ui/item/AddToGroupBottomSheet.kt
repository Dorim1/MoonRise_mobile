package com.example.moonrise.ui.item

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moonrise.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.moonrise.databinding.FragmentAddToGroupBinding

class AddToGroupBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentAddToGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddToGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}