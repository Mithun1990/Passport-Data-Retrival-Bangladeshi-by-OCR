package com.naim.testml

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.naim.testml.ui.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PassportPresenter(private val mainActivity: MainActivity) : PassportView.PresenterView {

    private var permissionFlag = false
    var dataMap = HashMap<String, String>()
    lateinit var passportModel: PassportModel

    init {
        onGettingPermission()
        passportModel = PassportModel(this)
    }

    override fun grantPermission(): Boolean {
        return permissionFlag
    }

    override fun onCaptureClicked(CAMERA_REQUEST: Int, file: File) {
        if (!permissionFlag) {
            mainActivity.setToast(mainActivity.getString(R.string.denied))
            return
        }
        dispatchTakePictureIntent(CAMERA_REQUEST, file)
    }

    override fun onGettingBitmapForImageView(bitmap: Bitmap) {
        mainActivity.showDialog()
        mainActivity.setBitmapOnImageView(bitmap)
        onGettingVisionBitmapAnalysis(bitmap)
    }

    override fun onGettingBitmapURIForCrop(bitmapURI: Uri) {
        CropImage.activity(bitmapURI)
            .setBackgroundColor(R.color.crop_shade)
            .setActivityTitle(R.string.cropping.toString())
            .start(mainActivity)
    }

    fun processPassportData(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizeText(image)
        } catch (e: Exception) {
        }
    }

    private fun onGettingVisionBitmapAnalysis(bitmap: Bitmap) {
        processPassportData(bitmap)
    }

    private fun onGettingPermission() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Permissions.check(
            mainActivity/*context*/,
            permissions,
            null/*options*/,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    permissionFlag = true
                }
            })
    }

    private fun dispatchTakePictureIntent(CAMERA_REQUEST: Int, file: File) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mainActivity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = file
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    mainActivity,
                    mainActivity.applicationContext.packageName +
                            ".com.naim.testml.GenericFileProvider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                mainActivity.startActivityForResult(takePictureIntent, CAMERA_REQUEST)
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
        return image
    }

    private fun recognizeText(image: InputImage) {
        // [START get_detector_default]
        val recognizer = TextRecognition.getClient()
        // [END get_detector_default]

        // [START run_detector]
        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d("TextRecognitionTest", "Result: $visionText")
                processPassportData(visionText)
            }
            .addOnFailureListener { e ->
                Log.d("TextRecognitionTest", "Result: ${e.localizedMessage}")
            }
    }

    private fun processPassportData(result: Text) {
        try {
            for (block in result.textBlocks) {
                for (line in block.lines) {
                    passportModel.retrievePassportInfo(line.text)
                }
            }
            Log.d("Passport Data", dataMap.toString())
            mainActivity.setPassportData(dataMap)
        } catch (e: Exception) {
        }
    }

    override fun getEachPassportData(data: String, dataType: String) {
        if (!dataMap.containsKey(dataType)) {
            dataMap.put(dataType, data)
        }
    }
}