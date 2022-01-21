package com.projects.wallpaperkotlin.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.wallpaperkotlin.databinding.ItemFilterRvBinding
import com.projects.wallpaperkotlin.fragments.filter.SketchImage

class FilterAdapter(
    val sketchImage: SketchImage,
    val context: Context,
    private val listener: OnFilterClickListener
) :
    RecyclerView.Adapter<FilterAdapter.Vh>() {

    inner class Vh(private val filterBinding: ItemFilterRvBinding) :
        RecyclerView.ViewHolder(filterBinding.root) {
        fun onBind(position: Int) {
            filterBinding.apply {
                val imageAs = sketchImage.getImageAs(position, 80)
                image.setImageBitmap(imageAs)
                itemV.setOnClickListener {
                    listener.onFilterClick(position)
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh =
        Vh(ItemFilterRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int = 10

    interface OnFilterClickListener {
        fun onFilterClick(position: Int)
    }


}