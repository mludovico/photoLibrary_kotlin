package br.com.mludovico.photolibrary_kotlin

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var imageUri: Uri? = null

    companion object {
        private const val PICK_PERMISSION_CODE = 1000
        private const val IMAGE_PICK_CODE = 1001
        private const val CAMERA_PERMISSION_CODE = 1002
        private const val CAMERA_OPEN_CODE = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_button.setOnClickListener {
            pickClickHandler()
        }
        camera_button.setOnClickListener {
            cameraClickListener()
        }
    }

    private fun cameraClickListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                checkSelfPermission(Manifest.permission.CAMERA)
                ==
                PackageManager.PERMISSION_DENIED
                ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                requestPermissions(permission, PICK_PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto tirada com a camera")

        imageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_OPEN_CODE)
    }

    private fun pickClickHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                ==
                PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, PICK_PERMISSION_CODE)
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            image_view.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_OPEN_CODE) {
            image_view.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PICK_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permissão para galeria negada", Toast.LENGTH_SHORT).show()
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.size > 1
                    &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permissão para câmera negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}