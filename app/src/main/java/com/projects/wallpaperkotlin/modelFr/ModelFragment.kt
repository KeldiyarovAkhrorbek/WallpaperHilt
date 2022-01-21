package com.projects.wallpaperkotlin.modelFr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.adapters.PhotoAdapter
import com.projects.wallpaperkotlin.databinding.FragmentModelBinding
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.projects.wallpaperkotlin.models.Photo
import com.projects.wallpaperkotlin.viewmodels.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

@AndroidEntryPoint
class ModelFragment : Fragment() {
    private var param1: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    private var _binding: FragmentModelBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PhotoAdapter
    private val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModelBinding.inflate(inflater, container, false)
        adapter = PhotoAdapter(object : PhotoAdapter.OnPhotoItemClickListener {
            override fun onItemCLick(result: Photo) {
                val photoEntity = PhotoEntity(
                    id = result.id,
                    photoUrl = result.src.portrait,
                    liked = false,
                    width = result.width,
                    height = result.height,
                    photographer = result.photographer,
                    photographer_url = result.photographer_url
                )
                val bundle = Bundle()
                bundle.putSerializable("photo", photoEntity)
                findNavController().navigate(R.id.imageFragment, bundle)
            }
        })
        binding.recycler.adapter = adapter
        pic(param1!!)
        return binding.root
    }

    private fun pic(param: String) {
        lifecycleScope.launch {
            viewModel.loadSearched(param ?: "android")
                .catch {

                }.collect {
                    adapter.submitData(it)
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ModelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}