package com.naim.testml

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface PassportView {

    interface UIView {

        fun setPassportData(hashMap: HashMap<String, String>)

        fun setBitmapOnImageView(bitmap: Bitmap)

        fun setToast(message: String)

        fun showDialog()

        fun dismissDialog()

    }

    interface PresenterView {

        fun grantPermission(): Boolean

        fun onGettingBitmapForImageView(bitmap: Bitmap)

        fun onCaptureClicked(CAMERA_REQUEST: Int, file: File)

        fun onGettingBitmapURIForCrop(bitmapURI: Uri)
        fun getEachPassportData(data: String, dataType: String)
    }

    interface ModelView {
        fun retrievePassportInfo(data: String)
    }
}