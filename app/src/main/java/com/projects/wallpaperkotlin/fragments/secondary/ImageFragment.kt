package com.projects.wallpaperkotlin.fragments.secondary

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.muddzdev.quickshot.QuickShot
import com.projects.wallpaperkotlin.MainActivity
import com.projects.wallpaperkotlin.R
import com.projects.wallpaperkotlin.databinding.BottomDialogBinding
import com.projects.wallpaperkotlin.databinding.FragmentImageBinding
import com.projects.wallpaperkotlin.di.utils.DatabaseSingleResource
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.projects.wallpaperkotlin.viewmodels.DatabaseViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ImageFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var visible = true
    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DatabaseViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val bundle = arguments
        var photoEntity = bundle?.getSerializable("photo") as PhotoEntity

        animateFirstTime()

        lifecycleScope.launch {
            viewModel.getPhotoByUrl(photoEntity.photoUrl).catch {

            }.collect {
                when (it) {
                    is DatabaseSingleResource.Error -> {}
                    is DatabaseSingleResource.Success -> {
                        if (it.photoEntity != null)
                            photoEntity = it.photoEntity
                    }
                    is DatabaseSingleResource.Loading -> {}
                }
            }
        }

        if (!photoEntity.liked) {
            binding.imgLike.setImageResource(R.drawable.ic_liked)
            photoEntity.liked = false
        } else {
            binding.imgLike.setImageResource(R.drawable.ic_heart)
            photoEntity.liked = true
        }
        binding.iconBack.setOnClickListener {
            if (visible)
                findNavController().popBackStack() else {
                hidePutShowDownload()
            }
        }
        Picasso.get().load(photoEntity.photoUrl).placeholder(R.drawable.placeholder1)
            .into(binding.image)
        binding.iconShare.setOnClickListener { v ->
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = Picasso.get().load(photoEntity.photoUrl).get()
                if (bitmap != null)
                    shareImageAndText(bitmap, requireContext())
            }

        }

        binding.iconDownload.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = Picasso.get().load(photoEntity.photoUrl).get()
                val title = URLUtil.guessFileName(photoEntity.photoUrl, null, null)
                saveImageToStorage(requireContext(), title, bitmap)
            }

        }

        binding.iconAbout.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
            val bottomDialogBinding: BottomDialogBinding = BottomDialogBinding.inflate(
                layoutInflater
            )
            bottomDialogBinding.authorTv.text = "Author: " + photoEntity.photographer
            bottomDialogBinding.websiteTv.text = "Website: " + photoEntity.photographer_url
            bottomDialogBinding.sizeTv.text =
                "Size: " + photoEntity.height + "x" + photoEntity.width
            bottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.setContentView(bottomDialogBinding.getRoot())
            val radius = 25f
            bottomSheetDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (container != null) {
                bottomDialogBinding.blurViewDialog.setupWith(container)
                    .setBlurAlgorithm(RenderScriptBlur(requireContext()))
                    .setBlurRadius(radius)
                    .setBlurAutoUpdate(true)
                    .setHasFixedTransformationMatrix(true)
            }
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.show()
        }

        binding.iconPut.setOnClickListener {
            hideDownloadShowPut()
        }

        binding.iconHome.setOnClickListener { v ->
            val result = arrayOf<Bitmap?>(null)
            Picasso.get().load(
                photoEntity.photoUrl
            ).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    result[0] = bitmap
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(
                        result[0],
                        null,
                        false,
                        WallpaperManager.FLAG_SYSTEM
                    )
                    Toast.makeText(requireContext(), "Successfully set", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.iconCustomize.setOnClickListener {
            findNavController().navigate(
                R.id.action_imageFragment_to_filterFragment,
                Bundle().apply {
                    putSerializable("image", photoEntity)
                })
        }


        binding.iconLock.setOnClickListener {
            val result = arrayOf<Bitmap?>(null)
            Picasso.get().load(photoEntity.photoUrl).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    result[0] = bitmap
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(
                        result[0],
                        null,
                        false,
                        WallpaperManager.FLAG_LOCK
                    )
                    Toast.makeText(requireContext(), "Successfully set", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.iconBoth.setOnClickListener {
            val result = arrayOf<Bitmap?>(null)
            Picasso.get().load(
                photoEntity.photoUrl
            ).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    result[0] = bitmap
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(
                        result[0],
                        null,
                        false,
                        WallpaperManager.FLAG_LOCK or WallpaperManager.FLAG_SYSTEM
                    )
                    Toast.makeText(requireContext(), "Successfully set", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        binding.iconLike.setOnClickListener {
            if (photoEntity.liked) {
                binding.imgLike.setImageResource(R.drawable.ic_liked)
                photoEntity.liked = false
                lifecycleScope.launch {
                    viewModel.deletePhoto(photoEntity)
                }
            } else {
                binding.imgLike.setImageResource(R.drawable.ic_heart)
                photoEntity.liked = true
                lifecycleScope.launch {
                    viewModel.addPhoto(photoEntity)
                }
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.hideBlurView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity?)?.showBlurView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun saveImageToStorage(context: Context?, imageName: String?, bitmap: Bitmap?) {
        QuickShot.of(bitmap!!, context!!)
            .enableLogging()
            .setFilename(imageName)
            .setPath("4K Full Wallpaper")
            .toPNG()
            .setResultListener(object : QuickShot.QuickShotListener {
                override fun onQuickShotSuccess(path: String) {
                    Toast.makeText(context, "Successfully saved!", Toast.LENGTH_SHORT).show()
                }

                override fun onQuickShotFailed(path: String, errorMsg: String) {
                    Toast.makeText(context, "Error while saving image!", Toast.LENGTH_SHORT).show()
                }
            })
            .save()
    }

    private fun shareImageAndText(bitmap: Bitmap, context: Context) {

        val intent = Intent(Intent.ACTION_SEND).setType("image/*")

        val uri = bitmapToUri(bitmap, context)

        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")

        context.startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun bitmapToUri(bitmap: Bitmap, context: Context): Uri {
        return Uri.parse(
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "image",
                null
            )
        )
    }

    private fun animateFirstTime() {
        binding.apply {
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconBack)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconShare)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconAbout)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconDownload)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconPut)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconCustomize)
            YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconLike)
        }
    }

    private fun hideDownloadShowPut() {
        binding.apply {
            object : CountDownTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconShare)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconAbout)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconDownload)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconPut)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconCustomize)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconLike)
                    iconShare.visibility = View.GONE
                    iconAbout.visibility = View.GONE
                    iconDownload.visibility = View.GONE
                    iconPut.visibility = View.GONE
                    iconCustomize.visibility = View.GONE
                    iconLike.visibility = View.GONE
                }

                override fun onFinish() {
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconHome)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconBoth)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconLock)
                    iconHome.visibility = View.VISIBLE
                    iconBoth.visibility = View.VISIBLE
                    iconLock.visibility = View.VISIBLE
                }
            }.start()

            visible = false
        }
    }

    private fun hidePutShowDownload() {
        binding.apply {
            object : CountDownTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconHome)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconBoth)
                    YoYo.with(Techniques.FadeOutRight).duration(1500).playOn(iconLock)
                    iconHome.visibility = View.GONE
                    iconBoth.visibility = View.GONE
                    iconLock.visibility = View.GONE
                }

                override fun onFinish() {
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconShare)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconAbout)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconDownload)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconPut)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconCustomize)
                    YoYo.with(Techniques.ZoomIn).duration(1500).playOn(iconLike)
                    iconShare.visibility = View.VISIBLE
                    iconAbout.visibility = View.VISIBLE
                    iconDownload.visibility = View.VISIBLE
                    iconPut.visibility = View.VISIBLE
                    iconCustomize.visibility = View.VISIBLE
                    iconLike.visibility = View.VISIBLE
                }
            }.start()
        }

        visible = true
    }

}