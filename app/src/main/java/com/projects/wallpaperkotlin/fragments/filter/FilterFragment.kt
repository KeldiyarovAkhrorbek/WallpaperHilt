package com.projects.wallpaperkotlin.fragments.filter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.projects.wallpaperkotlin.*
import com.projects.wallpaperkotlin.adapters.FilterAdapter
import com.projects.wallpaperkotlin.databinding.FragmentFilterBinding
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.squareup.picasso.Picasso
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class FilterFragment : Fragment(), CoroutineScope {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    lateinit var imageModel: PhotoEntity
    private var clickInfo = -1
    private lateinit var orgBitmap: Bitmap
    private lateinit var bitmap: Bitmap
    private val job = Job()
    private var pos = 0
    private var prog = 8

    lateinit var filterAdapter: FilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        fragmentInAnim(binding.navigation, binding.filter)
        clickInfo = 0

        loadBlurViews()

        imageModel = arguments?.getSerializable("image") as PhotoEntity

        Picasso.get().load(imageModel.photoUrl).into(binding.image)

        CoroutineScope(Dispatchers.IO).launch {
            orgBitmap = Picasso.get().load(imageModel.photoUrl).get()
            bitmap = Picasso.get().load(imageModel.photoUrl).get()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sketchImage = SketchImage.Builder(requireContext(), orgBitmap).build()

        val imageAs = sketchImage.getImageAs(0, 80)
        binding.image.setImageBitmap(imageAs)

        binding.apply {

            binding.btnDownload.setOnClickListener { v ->
                val filterBitmap: Bitmap = loadFilterBitmap()
                val imageName: String = generateFileName()
                saveImageToStorage(context, imageName, filterBitmap)
            }


            binding.btnDone.setOnClickListener { v ->
                inAnimation(
                    binding.navigation,
                    binding.filter,
                    binding.view2Top,
                    binding.view2Bottom
                )
                clickInfo = 1
            }

            binding.btnBack2.setOnClickListener { v ->
                outAnimation(
                    binding.view2Top,
                    binding.view2Bottom,
                    binding.navigation,
                    binding.filter
                )
                clickInfo = 0
            }

            binding.btnInstall.setOnClickListener { v ->
                inAnimation(
                    binding.view2Top,
                    binding.view2Bottom,
                    binding.intallTop,
                    binding.installBottom
                )
                clickInfo = 2
            }

            binding.btnBackInstall.setOnClickListener { v ->
                outAnimation(
                    binding.intallTop,
                    binding.installBottom,
                    binding.view2Top,
                    binding.view2Bottom
                )
                clickInfo = 1
            }

            binding.btnInstallLock.setOnClickListener { v ->
                val filterBitmap: Bitmap = loadFilterBitmap()
                setToWallPaper(context, filterBitmap, InstallType.FLAG_LOCK)
            }

            binding.btnInstallHome.setOnClickListener { v ->
                val filterBitmap: Bitmap = loadFilterBitmap()
                setToWallPaper(context, filterBitmap, InstallType.FLAG_SYSTEM)
            }

            binding.btnInstallAll.setOnClickListener { v ->
                val filterBitmap: Bitmap = loadFilterBitmap()
                setToWallPaper(context, filterBitmap, InstallType.FLAG_SYSTEM_LOCK)
            }

            binding.btnExit.setOnClickListener { v ->
                fragmentOutAnim(requireView(), binding.navigation, binding.filter)
                clickInfo = -1
            }


            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, process: Int, p2: Boolean) {
                    launch {
                        prog = process
                        val imageAs1 = sketchImage.getImageAs(pos, process * 10)
                        image.setImageBitmap(imageAs1)
                    }

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            val SI = SketchImage.Builder(requireContext(), bitmap).build()

            filterAdapter = FilterAdapter(SI, requireContext(), object :
                FilterAdapter.OnFilterClickListener {
                override fun onFilterClick(position: Int) {
                    launch {
                        pos = position
                        val imageAs3 = sketchImage.getImageAs(pos, prog * 10)
                        binding.seekBar.progress = prog
                        binding.image.setImageBitmap(imageAs3)
                    }
                }
            })

            binding.filterRv.adapter = filterAdapter

        }
    }

    private fun loadFilterBitmap(): Bitmap {
        val drawable1 = binding.image.drawable as BitmapDrawable
        return drawable1.bitmap
    }

    private fun loadBlurViews() {
        loadBlur(binding.done)
        loadBlur(binding.exit)
        loadBlur(binding.back2)
        loadBlur(binding.backInstall)
        loadBlur(binding.download)
        loadBlur(binding.install)
        loadBlur(binding.installAll)
        loadBlur(binding.installHome)
        loadBlur(binding.installLock)
    }

    private fun loadBlur(blurView: BlurView) {
        val radius = 20f
        blurView.setupWith(binding.root)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setOverlayColor(Color.parseColor("#40000000"))
            .setHasFixedTransformationMatrix(false)
    }

    override val coroutineContext: CoroutineContext
        get() = job

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.hideBlurView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity?)?.showBlurView()
    }
}