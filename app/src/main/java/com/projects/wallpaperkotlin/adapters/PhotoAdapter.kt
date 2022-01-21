package com.projects.wallpaperkotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.databinding.ItemPhotoBinding
import com.projects.wallpaperkotlin.models.Photo
import com.squareup.picasso.Picasso

class PhotoAdapter(val onPhotoItemClickListener: OnPhotoItemClickListener) :
    PagingDataAdapter<Photo, PhotoAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    inner class Vh(private val itemImageBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root) {

        fun onBind(randomData: Photo) {
            itemImageBinding.apply {
                Picasso.get().load(randomData.src.tiny)
                    .placeholder(R.drawable.placeholder1)
                    .into(image)

                itemView.setOnClickListener {
                    onPhotoItemClickListener.onItemCLick(randomData)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface OnPhotoItemClickListener {
        fun onItemCLick(result: Photo)
    }
}