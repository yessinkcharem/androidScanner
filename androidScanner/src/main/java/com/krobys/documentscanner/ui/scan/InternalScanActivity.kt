/**
Copyright 2021 Krobys

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package com.krobys.documentscanner.ui.scan

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.krobys.documentscanner.R
import com.krobys.documentscanner.common.extensions.hide
import com.krobys.documentscanner.common.extensions.show
import com.krobys.documentscanner.manager.SessionManager
import com.krobys.documentscanner.model.DocumentScannerErrorModel
import com.krobys.documentscanner.model.ScannerResults
import com.krobys.documentscanner.ui.camerascreen.CameraScreenFragment
import com.krobys.documentscanner.ui.components.ProgressView
import com.krobys.documentscanner.ui.imagecrop.ImageCropFragment
import com.krobys.documentscanner.ui.imageprocessing.ImageProcessingFragment
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import id.zelory.compressor.extension
import id.zelory.compressor.saveBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class InternalScanActivity : AppCompatActivity() {

    abstract fun onError(error: DocumentScannerErrorModel)
    abstract fun onSuccess(scannerResults: ScannerResults)
    abstract fun onClose()

    companion object {


        private val TAG = InternalScanActivity::class.simpleName
        internal const val CAMERA_SCREEN_FRAGMENT_TAG = "CameraScreenFragmentTag"
        internal const val IMAGE_CROP_FRAGMENT_TAG = "ImageCropFragmentTag"
        internal const val IMAGE_PROCESSING_FRAGMENT_TAG = "ImageProcessingFragmentTag"
        internal  val ORIGINAL_IMAGE_NAME = "original"
//        internal val CROPPED_IMAGE_NAME = "IMG_$timeStamp"
        internal  val TRANSFORMED_IMAGE_NAME = "transformed"
        internal const val NOT_INITIALIZED = -1L
    }

    //File

    internal lateinit var originalImageFile: File
    internal var croppedImage: Bitmap? = null
    internal var transformedImage: Bitmap? = null
    private var imageQuality: Int = 100
    private var imageSize: Long = NOT_INITIALIZED
    private lateinit var imageType: Bitmap.CompressFormat
    internal var shouldCallOnClose = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate : internalScanActivity")
        val sessionManager = SessionManager(this)
        imageType = sessionManager.getImageType()
        imageSize = sessionManager.getImageSize()
        imageQuality = sessionManager.getImageQuality()
        reInitOriginalImageFile()
    }

    internal fun reInitOriginalImageFile() {
        println("reInitOriginalImageFile")
        originalImageFile = File(filesDir, "${ORIGINAL_IMAGE_NAME}.${imageType.extension()}")
//        originalImageFile.delete()
    }
    //finalScannerResult

    private fun showCameraScreen() {
        println("showCameraScreen : internalScanActivity")
        val cameraScreenFragment = CameraScreenFragment.newInstance()
        addFragmentToBackStack(cameraScreenFragment, CAMERA_SCREEN_FRAGMENT_TAG)
    }

    internal fun showImageCropFragment() {
        println("showImageCropFragment : internalScanActivity")
        val imageCropFragment = ImageCropFragment.newInstance()
        addFragmentToBackStack(imageCropFragment, IMAGE_CROP_FRAGMENT_TAG)
    }

    internal fun showImageProcessingFragment() {
        println("showImageProcessingFragment : internalScanActivity")
        val imageProcessingFragment = ImageProcessingFragment.newInstance()
        addFragmentToBackStack(imageProcessingFragment, IMAGE_PROCESSING_FRAGMENT_TAG)
    }

    internal fun closeCurrentFragment() {
        println("closeCurrentFragment : internalScanActivity")
        supportFragmentManager.popBackStackImmediate()
    }

    private fun addFragmentToBackStack(fragment: Fragment, fragmentTag: String) {
        println("addFragmentToBackStack : internalScanActivity")
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.zdcContent, fragment, fragmentTag)
        if (supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
            fragmentTransaction.addToBackStack(fragmentTag)
        }
        fragmentTransaction.commit()
    }

    internal fun finalScannerResult() {
        println("finalScannerResult : internalScanActivity")
        compressFiles()
    }

    private fun compressFiles() {
        println("compressFiles : internalScanActivity")
        Log.d(TAG, "ZDCcompress starts ${System.currentTimeMillis()}")
        findViewById<ProgressView>(R.id.zdcProgressView).show()
        GlobalScope.launch(Dispatchers.IO) {

            val imageFileSave = when(true){
                transformedImage != null -> {
                    var transformedImageFile: File? = null
                    transformedImage?.let {
                        transformedImageFile = File(filesDir, "${TRANSFORMED_IMAGE_NAME}.${imageType.extension()}")
                        saveBitmap(it, transformedImageFile!!, imageType, imageQuality)


                    }
                    transformedImageFile = transformedImageFile?.let {
                        Compressor.compress(this@InternalScanActivity, it) {
                            quality(imageQuality)
                            if (imageSize != NOT_INITIALIZED) size(imageSize)
                            format(imageType)
                        }


                    }
                    println("InternalScanActivity - transformedImageFile  : " + transformedImageFile)
                    transformedImageFile
                }



                croppedImage != null -> {
                    var croppedImageFile: File? = null
                    croppedImage?.let {
                        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        val CROPPED_IMAGE_NAME = "IMG_$timeStamp"
                        croppedImageFile =
                            File(filesDir, "${CROPPED_IMAGE_NAME}.${imageType.extension()}")
                        saveBitmap(it, croppedImageFile!!, imageType, imageQuality)
                    }
                    croppedImageFile = croppedImageFile?.let {
                        Compressor.compress(this@InternalScanActivity, it) {
                            quality(imageQuality)
                            if (imageSize != NOT_INITIALIZED) size(imageSize)
                            format(imageType)
                        }
                    }
                    println("InternalScanActivity - croppedImageFile  : " + croppedImageFile)
                    croppedImageFile
                }
                else -> {
                    originalImageFile =
                        Compressor.compress(this@InternalScanActivity, originalImageFile) {
                            quality(imageQuality)
                            if (imageSize != NOT_INITIALIZED) size(imageSize)
                            format(imageType)
                        }
                    println("InternalScanActivity - originalImageFile  : " + originalImageFile)
                    originalImageFile
                }
            }

            val scannerResults = ScannerResults(imageFileSave)
            runOnUiThread {
                //findViewById<FrameLayout>(R.id.zdcContent).hide()
                findViewById<ProgressView>(R.id.zdcProgressView).hide()
                //shouldCallOnClose = false
                //supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                //shouldCallOnClose = true
                println("InternalScanActivity - scannerResults  : " + scannerResults)
                onSuccess(scannerResults)
                Log.d(TAG, "ZDCcompress ends ${System.currentTimeMillis()}")
            }
        }
    }

    internal fun addFragmentContentLayoutInternal() {
        println("addFragmentContentLayoutInternal : internalScanActivity")

        val frameLayout = FrameLayout(this)
        frameLayout.id = R.id.zdcContent
        addContentView(
            frameLayout, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val progressView = ProgressView(this)
        progressView.id = R.id.zdcProgressView
        addContentView(
            progressView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        progressView.hide()

        showCameraScreen()
    }
}
//delete
