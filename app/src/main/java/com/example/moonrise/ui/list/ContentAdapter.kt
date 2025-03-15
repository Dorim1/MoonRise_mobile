package com.example.moonrise.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moonrise.Content
import com.example.moonrise.R

class ContentAdapter : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private var contentList: List<Content> = emptyList()

    fun setContentList(newList: List<Content>) {
        contentList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_in_list, parent, false)
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val content = contentList[position]
        holder.bind(content)
    }

    override fun getItemCount(): Int = contentList.size

    class ContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.item_list_title)
        private val imageView: ImageView = view.findViewById(R.id.item_list_image)

        fun bind(content: Content) {
            titleTextView.text = content.title

            Glide.with(itemView.context)
                .load(content.image) // Загружаем изображение с URL
                .error(R.drawable.error_image) // Если ошибка загрузки
                .into(imageView)
        }
    }
}