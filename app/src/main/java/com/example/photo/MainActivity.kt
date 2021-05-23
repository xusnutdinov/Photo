package com.example.photo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photo.Constants.REQUEST_CAMERA_PERMISSION


class MainActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var btnTakePhoto: Button
    private lateinit var cameraSession: CameraSession
    private lateinit var btnGallery: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView = findViewById(R.id.textureView)
        btnTakePhoto = findViewById(R.id.btn_take_picture_2)
        btnGallery = findViewById(R.id.btn_gallery)

        cameraSession = CameraSession(this, textureView)

        btnTakePhoto.setOnClickListener {
            takePicture()
        }

        btnGallery.setOnClickListener {
            val intent = Intent(applicationContext, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(
                    this,
                    "No permission granted",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun takePicture(){
        cameraSession.takePicture()
    }

    override fun onResume() {
        super.onResume()
        cameraSession.resumeSession()
    }

    override fun onPause() {
        super.onPause()
        cameraSession.pauseSession()
    }
}