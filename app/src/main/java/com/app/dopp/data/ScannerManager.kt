package com.app.dopp.data

import android.content.Context
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class ScannerManager(context: Context) {
    private val scanner = GmsBarcodeScanning.getClient(context)

    fun startScanning(onResult: (String?) -> Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // barcode.rawValue содержит текст (например, ссылку на модель)
                onResult(barcode.rawValue)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}