package com.example.moonrise.ui.list

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.request.target.Target
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.moonrise.ContentDiffCallback
import com.example.moonrise.R
import com.example.moonrise.data.local.entity.ContentWithCategory

class ContentAdapter(private val navController: NavController) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private var contentWithCategoryList: List<ContentWithCategory> = emptyList()

    fun setContentList(newList: List<ContentWithCategory>) {
        val diffCallback = ContentDiffCallback(contentWithCategoryList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        contentWithCategoryList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val contentWithCategory = contentWithCategoryList[position]
        holder.bind(contentWithCategory)

        holder.itemView.setOnClickListener {
            // Передаем данные в следующий фрагмент через аргументы
            val bundle = Bundle().apply {
                putInt("contentId", contentWithCategory.content.id) // Передаем ID контента
            }
            navController.navigate(R.id.navigation_item, bundle)
        }
    }

    override fun getItemCount(): Int = contentWithCategoryList.size

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.item_list_title)
        private val imageView: ImageView = view.findViewById(R.id.item_list_image)
        private val categoryTextView: TextView = view.findViewById(R.id.item_list_category)
        private val loadingAnimation: LottieAnimationView = view.findViewById(R.id.loading_animation)

        fun bind(contentWithCategory: ContentWithCategory) {
            val content = contentWithCategory.content
            titleTextView.text = content.title
            categoryTextView.text = contentWithCategory.category.name  // Устанавливаем категорию

            // Показываем анимацию загрузки
            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()
            imageView.visibility = View.INVISIBLE

            Glide.with(itemView.context)
                .load(content.image)
                .error(R.drawable.error_image) // Изображение ошибки
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingAnimation.pauseAnimation()
                        loadingAnimation.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        imageView.setImageResource(R.drawable.error_image)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingAnimation.pauseAnimation()
                        loadingAnimation.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(imageView)
        }
    }
}
