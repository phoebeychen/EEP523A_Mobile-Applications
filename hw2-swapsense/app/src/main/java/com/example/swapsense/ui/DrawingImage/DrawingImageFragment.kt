package com.example.swapsense.ui.DrawingImage

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.swapsense.R
import android.graphics.Color
import androidx.exifinterface.media.ExifInterface
import android.graphics.Matrix

class DrawingImageFragment : Fragment() {
    private lateinit var drawingImageView: DrawingImageView
    private var originalBitmap: Bitmap? = null
    private var currentImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            currentImageUri = it

            try {
                // TODO: Load bitmap from URI, handle EXIF orientation, rotate if necessary, and set to drawingImageView
                // Hint: Use ExifInterface to get orientation and Matrix to rotate
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // 用 ExifInterface 读取方向
                val inputStream2 = requireContext().contentResolver.openInputStream(it)
                val exif = inputStream2?.let { stream -> ExifInterface(stream) }
                inputStream2?.close()

                val orientation = exif?.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                ) ?: ExifInterface.ORIENTATION_NORMAL

                // 用 Matrix 旋转
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0,
                    bitmap.width, bitmap.height,
                    matrix, true
                )

                // 保存原始bitmap用于重置
                originalBitmap = rotatedBitmap
                drawingImageView.setImageBitmap(rotatedBitmap)

            } catch (e: Exception) {
                // TODO: Handle Exception
                Toast.makeText(
                    requireContext(),
                    "Failed to load image: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // Add: 请求图库权限的launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            selectImageLauncher.launch("image/*")
        } else {
            Toast.makeText(
                requireContext(),
                "Storage permission is required to select images",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_draw, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawingImageView = view.findViewById(R.id.drawingImageView)

        // TODO: Launch image picker
        view.findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // TODO: Save drawn image as new image in gallery
        view.findViewById<Button>(R.id.btnSaveImage).setOnClickListener {
            saveImageAsNew()
        }

        // TODO: Reset the drawing to originalBitmap
        view.findViewById<Button>(R.id.btnReset).setOnClickListener {
            originalBitmap?.let {
                drawingImageView.setImageBitmap(it)
            } ?: Toast.makeText(
                requireContext(),
                "Please select one image first",
                Toast.LENGTH_SHORT
            ).show()
        }

        // TODO: Set brush color to black
        view.findViewById<Button>(R.id.btnColorBlack).setOnClickListener {
            drawingImageView.setBrushColor(Color.BLACK)
        }

        // TODO: Set brush color to red
        view.findViewById<Button>(R.id.btnColorRed).setOnClickListener {
            drawingImageView.setBrushColor(Color.RED)
        }

        // 蓝色画笔
        view.findViewById<Button>(R.id.btnColorBlue).setOnClickListener {
            drawingImageView.setBrushColor(Color.BLUE)
        }

        // 绿色画笔
        view.findViewById<Button>(R.id.btnColorGreen).setOnClickListener {
            drawingImageView.setBrushColor(Color.GREEN)
        }
    }

    private fun saveImageAsNew() {
        // TODO: Get bitmap with drawing from view
        val bitmap = drawingImageView.getBitmapWithDrawing() ?: run {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Create content values and insert into MediaStore
        val fileName = "DrawingImage_${System.currentTimeMillis()}"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SwapSense")
            }
        }

        val uri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        // TODO: Call saveImageToUri with returned URI
        uri?.let { saveImageToUri(it, bitmap) } ?: Toast.makeText(
            requireContext(),
            "Failed to save image",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveImageToUri(uri: Uri, bitmap: Bitmap) {
        try {
            // TODO: Compress and write bitmap to output stream using JPEG format
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
            }
            // TODO: Handle success and error with Toast messages
            Toast.makeText(requireContext(), "The image has been saved to the album", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Save failed\n: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}