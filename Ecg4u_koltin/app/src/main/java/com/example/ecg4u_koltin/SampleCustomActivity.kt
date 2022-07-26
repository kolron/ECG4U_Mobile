package com.example.ecg4u_koltin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.example.ecg4u_koltin.R
import com.example.ecg4u_koltin.databinding.ExtendedActivityBinding
import java.util.jar.Manifest

internal class SampleCustomActivity : CropImageActivity() {

    companion object {
        fun start(activity: Activity) {
            ActivityCompat.startActivity(
                activity,
                Intent(activity, SampleCustomActivity::class.java),
                null
            )
        }
    }

    private lateinit var binding: ExtendedActivityBinding
    private var counter = 0

    private var final_res: CropImage.ActivityResult? = null
    private val MY_CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ExtendedActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        updateRotationCounter(counter.toString())

        binding.saveBtn.setOnClickListener { cropImage() } // CropImageActivity.cropImage()
        binding.backBtn.setOnClickListener { onBackPressed() } // CropImageActivity.onBackPressed()
        binding.rotateText.setOnClickListener { onRotateClick() }
        binding.getResults.setOnClickListener { onDiagnoseClick() }

        setCropImageView(binding.cropImageView)

    }





    override fun showImageSourceDialog(openSource: (Source) -> Unit) {
        // Override this if you wanna a custom dialog layout
        super.showImageSourceDialog(openSource)
    }

    override fun setContentView(view: View) {
        // Override this to use your custom layout
        super.setContentView(binding.root)
    }

    private fun updateRotationCounter(counter: String) {
        binding.rotateText.text = getString(R.string.rotation_value, counter)
    }

    override fun onPickImageResult(resultUri: Uri?) {
        super.onPickImageResult(resultUri)

        if (resultUri != null) binding.cropImageView.setImageUriAsync(resultUri)
    }

    // Override this to add more information into the intent
    override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
        val result = super.getResultIntent(uri, error, sampleSize)
        return result.putExtra("EXTRA_KEY", "Extra data")
    }

    override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val result = CropImage.ActivityResult(
            originalUri = binding.cropImageView.imageUri,
            uriContent = uri,
            error = error,
            cropPoints = binding.cropImageView.cropPoints,
            cropRect = binding.cropImageView.cropRect,
            rotation = binding.cropImageView.rotatedDegrees,
            wholeImageRect = binding.cropImageView.wholeImageRect,
            sampleSize = sampleSize

        )
        final_res = result

        Log.v("File Path", result.getUriFilePath(this).toString())
        binding.cropImageView.setImageUriAsync(result.uriContent)
    }

    override fun setResultCancel() {
        Log.i("extend", "User this override to change behaviour when cancel")
        super.setResultCancel()
    }

    override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        Log.i(
            "extend",
            "If not using your layout, this can be one option to change colours. Check README and wiki for more"
        )
        super.updateMenuItemIconColor(menu, itemId, color)
    }

    private fun onRotateClick() {
        counter += 15
        binding.cropImageView.rotateImage(270)
        if (counter == 360) counter = 0
        updateRotationCounter(counter.toString())
    }

    private fun onDiagnoseClick() {
        val image_location = final_res?.getUriFilePath(this).toString()
        Log.d("Image Locaton", image_location)
        if (image_location == "null") Toast.makeText(
            this,
            "Please press Save / Crop before diagonsing",
            Toast.LENGTH_SHORT
        ).show()
        else {
            val intent = Intent(this, DisplayResultsActivity::class.java).apply {
                putExtra("ecg4u_location", image_location)
            }
            startActivity(intent)
        }
    }
}