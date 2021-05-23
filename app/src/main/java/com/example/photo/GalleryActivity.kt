package com.example.photo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photo.Constants.REQUEST_IMAGE
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


open class GalleryActivity : AppCompatActivity() {
    private lateinit var btnLoadImage: Button
    private lateinit var btnSaveImage: Button
    private lateinit var imageResult: ImageView

    private lateinit var source: Uri
    private var bitmap: Bitmap? = null
    private lateinit var canvas: Canvas

    var prvX: Float = 0.0F
    var prvY: Float = 0.0F

    private lateinit var draw: Paint

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        btnLoadImage = findViewById<View>(R.id.loadimage) as Button
        btnSaveImage = findViewById<View>(R.id.saveimage) as Button
        imageResult = findViewById<View>(R.id.imageView) as ImageView

        btnLoadImage.setOnClickListener {
//            val choosePhoto = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            startActivityForResult(choosePhoto, REQUEST_IMAGE)
            val choosePhoto = Intent()
            choosePhoto.type = "image/*"
            choosePhoto.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(choosePhoto, "Select Picture"), REQUEST_IMAGE)
        }

        // Задаем параметры для рисования
        draw = Paint()
        draw.style = Paint.Style.FILL
        draw.color = Color.RED
        draw.strokeWidth = 10f

        // Сохраняем при нажатии на кнопку
        btnSaveImage.setOnClickListener {
            bitmap?.let { saveBitmap(it) }
        }

        // Отлавливаем нажатия на ImageView
        imageResult.setOnTouchListener { v, event ->
            val action = event.action
            val x = event.x
            val y = event.y
            when (action) {
                // Когда нажимаем на экран
                MotionEvent.ACTION_DOWN -> {
                    prvX = x
                    prvY = y
                    drawOnBitmap(v as ImageView, bitmap!!, prvX, prvY, x, y)
                }
                // Когда ведем пальцем
                MotionEvent.ACTION_MOVE -> {
                    drawOnBitmap(v as ImageView, bitmap!!, prvX, prvY, x, y)
                    prvX = x
                    prvY = y
                }
                // Когда отпускаем палец
                MotionEvent.ACTION_UP -> drawOnBitmap(v as ImageView, bitmap!!, prvX, prvY, x, y)
            }
            return@setOnTouchListener true
        }
    }

    private fun drawOnBitmap(imageView: ImageView, bitmap: Bitmap,
                             x0: Float, y0: Float, x: Float, y: Float) {
        if (x < 0 || y < 0 || x > imageView.width || y > imageView.height) {
            return
        } else {
            // Получаем коэффицент разинцы между bitmap и imageView нашего холста
            val ratioWidth = bitmap.width.toFloat() / imageView.width.toFloat()
            val ratioHeight = bitmap.height.toFloat() / imageView.height.toFloat()
            // Соединяем точки линией
            canvas.drawLine(
                    x0 * ratioWidth,
                    y0 * ratioHeight,
                    x * ratioWidth,
                    y * ratioHeight,
                    draw)
            // Перерисовываем наш ImageView
            imageResult.invalidate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val tempBitmap: Bitmap
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                // Если получаем нужный Request_code
                REQUEST_IMAGE -> {
                    // Получаем ссылку на изображение из Intent
                    source = data?.data!!
                    try {
                        //Создаем и прорисовываем Bitmap из нашего изображения
                        tempBitmap = BitmapFactory.decodeStream(
                                contentResolver.openInputStream(source))
                        val config: Bitmap.Config = if (tempBitmap.config != null) {
                            tempBitmap.config
                        } else {
                            Bitmap.Config.ARGB_8888
                        }
                        bitmap = Bitmap.createBitmap(
                                tempBitmap.width,
                                tempBitmap.height,
                                config)
                        canvas = Canvas(bitmap!!)
                        canvas.drawBitmap(tempBitmap, 0f, 0f, null)
                        imageResult.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(this,
                                "Ошибка: " + e.message,
                                Toast.LENGTH_LONG).show()
                    } catch (e: IOException) {
                        Toast.makeText(this,
                                "Ошибка: " + e.message,
                                Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        //Сохраняем наше редактированное изображение
        val simpleDateFormat = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.getDefault())
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val name = simpleDateFormat.format(Calendar.getInstance().time) + ".jpg"
        val file = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val newFile = File(file, name)
        try {
            val fileOutputStream = FileOutputStream(newFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Toast.makeText(this,
                    "Сохранение: $name",
                    Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this,
                    "Ошибка: " + e.message,
                    Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(this,
                    "Ошибка: " + e.message,
                    Toast.LENGTH_LONG).show()
        }
    }
}