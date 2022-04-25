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


package com.krobys.documentscanner.ui

import android.content.Context
import android.graphics.Bitmap
import com.krobys.documentscanner.manager.SessionManager

object DocumentScanner {

    fun init(context: Context, configuration: Configuration = Configuration()) {
        System.loadLibrary("opencv_java4")
        val sessionManager = SessionManager(context)
        if(configuration.imageQuality in 1..500) {
            sessionManager.setImageQuality(configuration.imageQuality)
        }
        sessionManager.setImageSize(configuration.imageSize)
        sessionManager.setImageType(configuration.imageType)
        sessionManager.setGalleryEnabled(configuration.isGalleryEnabled);
        sessionManager.setMagicButtonEnabled(configuration.isMagicButtonEnabled);
        sessionManager.setCaptureModeButtonEnabled(configuration.isCaptureModeButtonEnabled);
        sessionManager.setAutoCaptureEnabledByDefault(configuration.isAutoCaptureEnabledByDefault);
        sessionManager.setLiveDetectionEnabled(configuration.isLiveDetectionEnabled);
        sessionManager.setCropperEnabled(configuration.isCropperEnabled);
    }


    data class Configuration(
        var imageQuality: Int = 500,
        var imageSize: Long = -1,
        var imageType: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        var isGalleryEnabled: Boolean = true,
        var isCropperEnabled: Boolean = true,
        var isCaptureModeButtonEnabled: Boolean = true,
        var isAutoCaptureEnabledByDefault: Boolean = true,
        var isLiveDetectionEnabled: Boolean = true,
        var isMagicButtonEnabled: Boolean = true,
    ){
    }
}