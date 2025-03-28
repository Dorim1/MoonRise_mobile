package com.example.moonrise.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.databinding.FragmentItemBinding

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var content: ContentWithCategory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Получаем переданный контент ID
        val contentId = arguments?.getInt("contentId") ?: return root

        // Здесь нужно получить данные контента по ID, например, из базы данных
        // Например, через ViewModel или напрямую через DAO
        loadContentDetails(contentId)

        return root
    }

    private fun loadContentDetails(contentId: Int) {
        // Ваш код для загрузки данных контента по ID
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}