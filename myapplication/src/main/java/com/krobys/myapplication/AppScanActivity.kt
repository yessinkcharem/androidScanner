package com.krobys.myapplication

import android.os.Bundle
import android.widget.Button
import com.krobys.documentscanner.ScanActivity
import com.krobys.documentscanner.model.DocumentScannerErrorModel
import com.krobys.documentscanner.model.ScannerResults

class AppScanActivity: ScanActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate : AppScanActivity")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_scan_activity_layout)

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener(){
            println("\n\n\n\n")
            println("click click")
            println("\n\n\n\n")
            addFragmentContentLayout()

        }



    }

    override fun onError(error: DocumentScannerErrorModel) {

    }

    override fun onSuccess(scannerResults: ScannerResults) {

        println("console on success")

        println("scannerResults")
        println(scannerResults)

        setContentView(R.layout.app_scan_activity_layout)
        val button: Button = findViewById(R.id.button)
        button.setOnClickListener(){
            println("\n\n\n\n")
            println("click click")
            println("\n\n\n\n")
            addFragmentContentLayout()

        }



    }

    override fun onClose() {

        println("onClose : AppScanActivity")
        finish()
    }
}