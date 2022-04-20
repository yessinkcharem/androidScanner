package com.krobys.myapplication

import android.app.Application
import android.graphics.Bitmap
import com.krobys.documentscanner.ui.DocumentScanner

class MyCustomApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val configuration = DocumentScanner.Configuration()
        configuration.imageQuality = 200
        configuration.imageSize = 3000000 // 1 MB
        configuration.imageType = Bitmap.CompressFormat.JPEG
        DocumentScanner.init(this, configuration) // or simply DocumentScanner.init(this)
    }
}