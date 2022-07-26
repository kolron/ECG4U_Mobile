package com.example.ecg4u_koltin

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecg4u_koltin.databinding.ActivityMainBinding
import android.Manifest

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sampleCustomActivity.setOnClickListener { SampleCustomActivity.start(this) }

        checkPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE)
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
    }
    private fun Fragment.show() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.container.id, this)
            .commit()
    }
    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(binding.container.id)?.apply {
            supportFragmentManager.beginTransaction().remove(this).commit()
            return
        }
        super.onBackPressed()
    }
    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }
}