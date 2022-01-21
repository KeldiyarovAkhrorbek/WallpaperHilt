package com.projects.wallpaperkotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.databinding.ItemPhotoBinding
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.squareup.picasso.Picasso

class LikedPhotoAdapter(val onPhotoItemClickListener: OnPhotoItemClickListener) :
    ListAdapter<PhotoEntity, LikedPhotoAdapter.Vh>(MyDiffUtil()) {

    class MyDiffUtil : DiffUtil.ItemCallback<PhotoEntity>() {
        override fun areItemsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
            return oldItem.photoUrl == newItem.photoUrl
        }

        override fun areContentsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
            return oldItem == newItem
        }
    }

    inner class Vh(private val itemImageBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root) {

        fun onBind(randomData: PhotoEntity) {
            itemImageBinding.apply {
                Picasso
                    .get()
                    .load(randomData.photoUrl)
                    .resize(100, 100)
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
        fun onItemCLick(photoEntity: PhotoEntity)
    }
}