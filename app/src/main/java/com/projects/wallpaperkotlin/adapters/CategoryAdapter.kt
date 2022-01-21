package com.projects.wallpaperkotlin.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.projects.wallpaperkotlin.modelFr.ModelFragment

class CategoryAdapter(var fragment: Fragment, var categoryList: ArrayList<String>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ModelFragment.newInstance(categoryList[position])
    }
}