package com.projects.wallpaperkotlin.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.adapters.CategoryAdapter
import com.projects.wallpaperkotlin.databinding.FragmentHomeBinding
import com.projects.wallpaperkotlin.databinding.ItemTabBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var categoryAdapter: CategoryAdapter
    private var param1: String? = null
    private var param2: String? = null
    private var categoryList: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        loadCategories()

        categoryAdapter = CategoryAdapter(this, categoryList)
        binding.viewpager.adapter = categoryAdapter
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab: TabLayout.Tab, position: Int ->
            val itemTabBinding: ItemTabBinding = ItemTabBinding.inflate(layoutInflater)
            tab.customView = itemTabBinding.getRoot()
            itemTabBinding.text.text = categoryList[position]
            if (position == 0) {
                with(itemTabBinding) {
                    circle.visibility = View.VISIBLE
                    text.setTextColor(Color.WHITE)
                }
            } else {
                itemTabBinding.circle.visibility = View.INVISIBLE
                itemTabBinding.text.setTextColor(Color.parseColor("#808a93"))
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                itemTabBinding.circle.visibility = View.VISIBLE
                itemTabBinding.text.setTextColor(Color.WHITE)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                itemTabBinding.circle.visibility = View.INVISIBLE
                itemTabBinding.text.setTextColor(Color.parseColor("#808a93"))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main1, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCategories() {
        categoryList = ArrayList()
        categoryList.add("Abstract")
        categoryList.add("Animals")
        categoryList.add("Technology")
        categoryList.add("Uzbekistan")
        categoryList.add("Laptop")
        categoryList.add("Cars")
        categoryList.add("Guns")
        categoryList.add("Mountain")
        categoryList.add("Horses")
        categoryList.add("Movies")
        categoryList.add("Girls")
    }
}