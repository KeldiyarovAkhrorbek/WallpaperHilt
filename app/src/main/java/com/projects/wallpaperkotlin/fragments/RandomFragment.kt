package com.projects.wallpaperkotlin.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.adapters.PhotoAdapter
import com.projects.wallpaperkotlin.databinding.FragmentRandomBinding
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.projects.wallpaperkotlin.models.Photo
import com.projects.wallpaperkotlin.viewmodels.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class RandomFragment : Fragment(), CoroutineScope {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var _binding: FragmentRandomBinding? = null
    private val binding get() = _binding!!
    private val job = Job()
    private lateinit var adapter: PhotoAdapter
    private val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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
        binding.rv.adapter = adapter
        random()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RandomFragment().apply {
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

    fun random() {
        lifecycleScope.launch {
            viewModel.loadCurated().catch {

            }.collect {
                adapter.submitData(it)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job
}