package com.example.swapsense.ui.Camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.Manifest
import com.example.swapsense.R
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    // 预览视图
    private lateinit var previewView: PreviewView

    // 拍照后显示的照片
    private lateinit var ivCapturedPhoto: android.widget.ImageView

    // 拍照用例，可为空（相机未启动时为null）
    private var imageCapture: ImageCapture? = null

    // 默认使用后置摄像头
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var tvPhotoPlaceholder: TextView

    // 根据 Android 版本返回需要的权限列表
    private val requiredPermissions: Array<String>
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ 用 READ_MEDIA_IMAGES
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                // Android 12 及以下用 READ/WRITE_EXTERNAL_STORAGE
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 注册权限请求launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // 检查权限结果
            if (handlePermissionResult(permissions)) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera and storage permissions are required to use this feature",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化预览视图
        previewView = view.findViewById(R.id.previewView)
        // 初始化拍照预览ImageView
        ivCapturedPhoto = view.findViewById(R.id.ivCapturedPhoto)
        tvPhotoPlaceholder = view.findViewById(R.id.tvPhotoPlaceholder)

        // 拍照按钮点击事件
        view.findViewById<Button>(R.id.btnCapture).setOnClickListener {
            takePhoto()
        }

        // 切换摄像头按钮点击事件
        view.findViewById<Button>(R.id.btnSwitchCamera).setOnClickListener {
            // 切换前后摄像头
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            // 重新启动相机以应用切换
            startCamera()
        }

        // 检查权限，有权限直接启动相机，没有则请求权限
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }

    // 检查所有权限是否已获得
    private fun allPermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 处理权限结果，Android 10+ 不需要 WRITE_EXTERNAL_STORAGE
    private fun handlePermissionResult(
        permissions: Map<String, Boolean>
    ): Boolean {
        return permissions.entries.all { (permission, granted) ->
            if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            ) {
                // Android 10+ 不需要写入权限，直接视为通过
                true
            } else {
                granted
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // 获取相机提供者
            val cameraProvider = cameraProviderFuture.get()

            // 构建预览用例
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // 构建拍照用例
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // 选择摄像头（前置或后置）
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                // 先解绑所有用例，再重新绑定
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraFragment", "Camera startup failure", e)
                Toast.makeText(
                    requireContext(),
                    "Camera startup failure: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // imageCapture 未初始化则直接返回
        val imageCapture = imageCapture ?: run {
            Toast.makeText(requireContext(), "camera is not ready", Toast.LENGTH_SHORT).show()
            return
        }

        // 生成文件名（用时间戳保证唯一）
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())

        // 设置保存到相册的参数
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Android 10+ 指定保存路径
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SwapSense")
            }
        }

        // 构建输出选项
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        // 执行拍照
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireContext(),
                        "Photo saved to gallery",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 拍照成功后显示在 ImageView 里
                    output.savedUri?.let { uri ->
                        requireActivity().runOnUiThread {
                            ivCapturedPhoto.setImageURI(uri)
                            tvPhotoPlaceholder.visibility = View.GONE
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed", exception)
                    Toast.makeText(
                        requireContext(),
                        "Photo capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}