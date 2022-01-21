package com.projects.wallpaperkotlin.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.adapters.LikedPhotoAdapter
import com.projects.wallpaperkotlin.databinding.FragmentLikedBinding
import com.projects.wallpaperkotlin.di.utils.DatabaseResource
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.projects.wallpaperkotlin.viewmodels.DatabaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class LikedFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentLikedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LikedPhotoAdapter
    private val viewModel: DatabaseViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        adapter = LikedPhotoAdapter(object : LikedPhotoAdapter.OnPhotoItemClickListener {
            override fun onItemCLick(photoEntity: PhotoEntity) {
                val bundle = Bundle()
                bundle.putSerializable("photo", photoEntity)
                findNavController().navigate(R.id.imageFragment, bundle)
            }
        })
        binding.recycler.adapter = adapter
        lifecycleScope.launch {
            val flow = viewModel.getAllPhotos()
            flow.catch {

            }.collect {
                when (it) {
                    is DatabaseResource.Loading -> {

                    }
                    is DatabaseResource.Success -> {
                        adapter.submitList(it.list)
                    }
                    is DatabaseResource.Error -> {

                    }
                }


            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LikedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}