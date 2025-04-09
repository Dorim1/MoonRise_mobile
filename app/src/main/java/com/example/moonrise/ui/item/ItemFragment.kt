package com.example.moonrise.ui.item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.moonrise.R
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.databinding.FragmentItemBinding

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val contentId = arguments?.getInt("contentId") ?: return root

        val database = AppDatabase.getDatabase(requireContext())
        val contentDao = database.contentDao()
        viewModel = ViewModelProvider(this, ItemViewModelFactory(contentDao))[ItemViewModel::class.java]

        viewModel.getContent(contentId).observe(viewLifecycleOwner) { contentWithCategory ->
            binding.titleItem.text = contentWithCategory.content.title
            binding.orTitleItem.text = contentWithCategory.content.orTitle
            binding.ageRating.text = contentWithCategory.content.ageRating
            binding.descriptionItem.text = contentWithCategory.content.description
            binding.releaseDate.text = getString(R.string.release_date, contentWithCategory.content.releaseDate)
            Glide.with(this).load(contentWithCategory.content.image).into(binding.imageItem)

            binding.category.text = getString(R.string.category_format, contentWithCategory.category.name)
        }

        viewModel.getGenres(contentId).observe(viewLifecycleOwner) { genres ->
            Log.d("ItemFragment", "Genres: $genres") // Логируем жанры
            val genreList = genres.joinToString(", ") { it.name }
            binding.genre.text = if (genreList.isNotEmpty()) {
                getString(R.string.genres_format, genreList)
            } else {
                getString(R.string.no_genres)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}