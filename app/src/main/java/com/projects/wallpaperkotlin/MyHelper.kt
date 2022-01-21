package com.projects.wallpaperkotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.muddzdev.quickshot.QuickShot
import com.muddzdev.quickshot.QuickShot.QuickShotListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


// <-----=============================================================================------------->
fun inAnimation(
    outTop: LinearLayout?,
    outBottom: LinearLayout?,
    inTop: LinearLayout,
    inBottom: LinearLayout
) {
    YoYo.with(Techniques.ZoomOutUp)
        .duration(500)
        .withListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                YoYo.with(Techniques.ZoomOutDown)
                    .duration(500)
                    .playOn(outBottom)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                inBottom.visibility = View.VISIBLE
                inTop.visibility = View.VISIBLE
                YoYo.with(Techniques.ZoomInDown)
                    .duration(500)
                    .playOn(inTop)
                YoYo.with(Techniques.ZoomInUp)
                    .duration(500)
                    .playOn(inBottom)
            }
        })
        .playOn(outTop)
}

// <-----=============================================================================------------->
fun outAnimation(
    outTop: LinearLayout?,
    outBottom: LinearLayout?,
    inTop: LinearLayout?,
    inBottom: LinearLayout?
) {
    YoYo.with(Techniques.ZoomOutUp)
        .duration(500)
        .withListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                YoYo.with(Techniques.ZoomOutDown)
                    .duration(500)
                    .playOn(outBottom)
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                YoYo.with(Techniques.ZoomInDown)
                    .duration(500)
                    .playOn(inTop)
                YoYo.with(Techniques.ZoomInUp)
                    .duration(500)
                    .playOn(inBottom)
            }
        }).playOn(outTop)
}

// <-----=============================================================================------------->
fun saveImageToStorage(context: Context?, imageName: String?, bitmap: Bitmap?) {
    QuickShot.of(bitmap!!, context!!)
        .enableLogging()
        .setFilename(imageName)
        .setPath("4K Full Wallpaper")
        .toPNG()
        .setResultListener(object : QuickShotListener {
            override fun onQuickShotSuccess(path: String) {
                Toast.makeText(context, path, Toast.LENGTH_SHORT).show()
            }

            override fun onQuickShotFailed(path: String, errorMsg: String) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        })
        .save()
}

// <-----=============================================================================------------->
fun fragmentInAnim(inTop: LinearLayout, inBottom: LinearLayout) {
    inTop.visibility = View.VISIBLE
    inBottom.visibility = View.VISIBLE
    YoYo.with(Techniques.ZoomInLeft)
        .duration(500)
        .playOn(inTop)
    YoYo.with(Techniques.ZoomInLeft)
        .duration(500)
        .playOn(inBottom)
}

// <-----=============================================================================------------->
fun fragmentOutAnim(view: View?, outTop: LinearLayout?, outBottom: LinearLayout?) {
//        binding.view1.setVisibility(View.VISIBLE);
//        binding.view2.setVisibility(View.VISIBLE);
    YoYo.with(Techniques.ZoomOutLeft)
        .duration(500)
        .playOn(outTop)
    YoYo.with(Techniques.ZoomOutLeft)
        .duration(500).withListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Navigation.findNavController(view!!).popBackStack()
                super.onAnimationEnd(animation)
            }
        })
        .playOn(outBottom)
}

// <-----=============================================================================------------->
@SuppressLint("SimpleDateFormat")
fun generateFileName(): String {
    val timeStamp =
        SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
    return "MI_$timeStamp"
}

// <-----=============================================================================------------->

//    <-----=================================================================-------->
fun shareImageAndText(bitmap: Bitmap, context: Context) {

    val intent = Intent(Intent.ACTION_SEND).setType("image/*")

    val uri = bitmapToUri(bitmap, context)

    // putting uri of image to be shared
    intent.putExtra(Intent.EXTRA_STREAM, uri)

    // adding text to share
    intent.putExtra(
        Intent.EXTRA_TEXT,
        "Amazing wallpaper for your device\nPowered by @mukhtorov712"
    )

    // Add subject Here
    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")


    // calling startActivity() to share
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

fun setToWallPaper(context: Context?, bitmap: Bitmap?, installType: InstallType?) {
    val wm = WallpaperManager.getInstance(context)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when (installType) {
                InstallType.FLAG_LOCK -> wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_LOCK
                )
                InstallType.FLAG_SYSTEM -> wm.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )
                InstallType.FLAG_SYSTEM_LOCK -> {
                    wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                }
            }
            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context, "Wallpaper not supported",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}


