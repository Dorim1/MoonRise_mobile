package com.example.moonrise.ui.item

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
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
import com.bumptech.glide.request.target.Target
import com.example.moonrise.R
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.ContentDiffCallback

class RelatedAdapter(private val navController: NavController) :
    RecyclerView.Adapter<RelatedAdapter.RelatedViewHolder>() {

    private var relatedContentList: List<ContentWithCategory> = emptyList()

    fun setRelatedContentList(newList: List<ContentWithCategory>) {
        val diffCallback = ContentDiffCallback(relatedContentList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        relatedContentList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_related, parent, false)
        return RelatedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RelatedViewHolder, position: Int) {
        val content = relatedContentList[position]
        holder.bind(content)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("contentId", content.content.id)
            }
            navController.navigate(R.id.navigation_item, bundle)
        }
    }

    override fun getItemCount(): Int = relatedContentList.size

    class RelatedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.related_title)
        private val imageView: ImageView = view.findViewById(R.id.item_related_image)
        private val loadingAnimation: LottieAnimationView = view.findViewById(R.id.loading_animation)

        fun bind(contentWithCategory: ContentWithCategory) {
            val content = contentWithCategory.content
            titleTextView.text = content.title

            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()
            imageView.visibility = View.INVISIBLE

            Glide.with(itemView.context)
                .load(content.image)
                .error(R.drawable.error_image)
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