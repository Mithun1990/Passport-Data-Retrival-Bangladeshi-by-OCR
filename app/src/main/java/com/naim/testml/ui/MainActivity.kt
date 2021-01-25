package com.naim.testml.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.naim.testml.PassportPresenter
import com.naim.testml.PassportView
import com.naim.testml.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main_test.*
import java.io.File


class MainActivity : AppCompatActivity(), PassportView.UIView {

    private val CAMERA_REQUEST = 1001
    private lateinit var presenter: PassportPresenter
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)
        //Initialize Presenter
        presenter = PassportPresenter(this@MainActivity)
        settingListeners()
    }


    override fun setPassportData(hashMap: HashMap<String, String>) {

    }

    override fun setBitmapOnImageView(bitmap: Bitmap) {
        capturedImage.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != CAMERA_REQUEST && resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            CAMERA_REQUEST -> {
                var uri = Uri.fromFile(file)
                presenter.onGettingBitmapURIForCrop(uri)
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                var uri = CropImage.getActivityResult(data).uri
                if (Build.VERSION.SDK_INT < 28) {
                    presenter.onGettingBitmapForImageView(
                        MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            uri
                        )
                    )
                } else {
                    val source: ImageDecoder.Source =
                        ImageDecoder.createSource(contentResolver, uri)
                    val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                    presenter.onGettingBitmapForImageView(
                        bitmap
                    )
                }
            }
        }
    }

    override fun setToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showDialog() {

    }

    override fun dismissDialog() {
    }

    private fun settingListeners() {
        btnOpenCamera.setOnClickListener {
            file = presenter.createImageFile()
            presenter.onCaptureClicked(CAMERA_REQUEST, file!!)
        }
    }
}
