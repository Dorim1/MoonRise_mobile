package com.example.moonrise.ui.item

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.moonrise.R
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.databinding.FragmentItemBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.core.graphics.drawable.toDrawable


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
        val statusDao = database.statusDao()
        val ratingDao = database.ratingDao()


        viewModel = ViewModelProvider(this, ItemViewModelFactory(contentDao, statusDao, ratingDao))[ItemViewModel::class.java]

        viewModel.getStatus(contentId).observe(viewLifecycleOwner) { statusEntity ->
            if (statusEntity != null) {
                binding.addButton.text = getString(R.string.edit_button)
            } else {
                binding.addButton.text = getString(R.string.add_button)
            }
        }


        val navController = findNavController()
        val relatedAdapter = RelatedAdapter(navController)
        binding.relatedRecyclerView.adapter = relatedAdapter
        binding.relatedRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.showMoreText.text = getString(R.string.show_more)

        binding.showMoreText.setOnClickListener {
            toggleDescription()
        }

        binding.addButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val statusEntity = statusDao.getStatusOnce(contentId)
                val currentStatus = statusEntity?.statusTypeId

                AddToGroupBottomSheet(contentId, currentStatus)
                    .show(parentFragmentManager, "AddToGroupBottomSheet")
            }
        }

        binding.rateButton.setOnClickListener {
            val ratingBottomSheet = AddRatingBottomSheet(
                onSaveRating = { rating ->
                    val intRating = rating.roundToInt()

                    if (intRating != 0) {
                        Toast.makeText(requireContext(), "Оценка: $intRating", Toast.LENGTH_SHORT).show()
                    }
                },
                contentId = contentId,
                viewModel = viewModel
            )
            ratingBottomSheet.show(parentFragmentManager, "RatingBottomSheetFragment")
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.getRating(contentId).collect { rating ->
//                    if (rating != null) {
//                        binding.rateText.text = rating.ratingValue.toInt().toString()
//                        binding.rateText.visibility = View.VISIBLE
//                    } else {
//                        binding.rateText.visibility = View.GONE
//                    }
//                }
//            }
//        }



        viewModel.getRelatedContent(contentId).observe(viewLifecycleOwner) { relatedContent ->
            binding.relatedBlock.visibility = if (relatedContent.isNotEmpty()) View.VISIBLE else View.GONE
            relatedAdapter.setRelatedContentList(relatedContent)
        }

        viewModel.getContent(contentId).observe(viewLifecycleOwner) { contentWithCategory ->
            binding.titleItem.text = contentWithCategory.content.title
            binding.orTitleItem.text = contentWithCategory.content.orTitle
            binding.ageRating.text = contentWithCategory.content.ageRating
            binding.descriptionItem.text = contentWithCategory.content.description
            binding.releaseDate.text = getString(R.string.release_date, "${contentWithCategory.content.releaseDate} г.")

            binding.titleItem.setOnClickListener {
                showCopyTitlesBottomSheet()
            }

            binding.orTitleItem.setOnClickListener {
                showCopyTitlesBottomSheet()
            }

            Glide.with(this)
                .load(contentWithCategory.content.image)
                .placeholder(ContextCompat.getColor(requireContext(), R.color.main_purple).toDrawable())
                .error(R.drawable.error_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.imageItem)


            binding.category.text = getString(R.string.category_format, contentWithCategory.category.name)

            val watchingUrl = contentWithCategory.content.watchingUrl
            if (!watchingUrl.isNullOrEmpty()) {
                binding.watchLabel.visibility = View.VISIBLE
                binding.watchButton.visibility = View.VISIBLE

                binding.watchButton.setOnClickListener {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, watchingUrl.toUri())
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

    private fun showCopyTitlesBottomSheet() {
        val title = binding.titleItem.text.toString()
        val orTitle = binding.orTitleItem.text.toString()
        CopyTitlesBottomSheet(title, orTitle).show(parentFragmentManager, "CopyTitlesBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.relatedRecyclerView.adapter = null
        _binding = null
    }
}