package com.example.moonrise.ui.item

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.moonrise.R
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.databinding.FragmentItemBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moonrise.ui.item.RelatedAdapter
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController


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
        val relatedContentDao = database.relatedContentDao()

        val navController = findNavController()
        val relatedAdapter = RelatedAdapter(navController)
        binding.relatedRecyclerView.adapter = relatedAdapter
        binding.relatedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.showMoreText.text = getString(R.string.show_more)

        binding.showMoreText.setOnClickListener {
            toggleDescription()
        }

        viewModel = ViewModelProvider(this, ItemViewModelFactory(contentDao, relatedContentDao))[ItemViewModel::class.java]

        viewModel.getRelatedContent(contentId).observe(viewLifecycleOwner) { relatedContent ->
            if (relatedContent.isNotEmpty()) {
                binding.relatedLabel.visibility = View.VISIBLE
                binding.relatedRecyclerView.visibility = View.VISIBLE
                relatedAdapter.setRelatedContentList(relatedContent)
            } else {
                binding.relatedLabel.visibility = View.GONE
                binding.relatedRecyclerView.visibility = View.GONE
            }
        }

        viewModel.getContent(contentId).observe(viewLifecycleOwner) { contentWithCategory ->
            binding.titleItem.text = contentWithCategory.content.title
            binding.orTitleItem.text = contentWithCategory.content.orTitle
            binding.ageRating.text = contentWithCategory.content.ageRating
            binding.descriptionItem.text = contentWithCategory.content.description
            binding.releaseDate.text = getString(R.string.release_date, contentWithCategory.content.releaseDate)
            Glide.with(this).load(contentWithCategory.content.image).into(binding.imageItem)
            viewModel.loadRelatedContentFromJson(requireContext())

            binding.category.text = getString(R.string.category_format, contentWithCategory.category.name)

            val watchingUrl = contentWithCategory.content.watchingUrl
            if (!watchingUrl.isNullOrEmpty()) {
                binding.watchLabel.visibility = View.VISIBLE
                binding.watchButton.visibility = View.VISIBLE

                binding.watchButton.setOnClickListener {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, watchingUrl.toUri())
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.watchLabel.visibility = View.GONE
                binding.watchButton.visibility = View.GONE
            }
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

    private fun toggleDescription() {
        val isExpanded = binding.descriptionItem.maxLines == Int.MAX_VALUE

        if (isExpanded) {
            binding.descriptionItem.maxLines = 8
            binding.descriptionItem.ellipsize = TextUtils.TruncateAt.END
            binding.showMoreText.text = getString(R.string.show_more)
        } else {
            binding.descriptionItem.maxLines = Int.MAX_VALUE
            binding.descriptionItem.ellipsize = null
            binding.showMoreText.text = getString(R.string.hide)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}