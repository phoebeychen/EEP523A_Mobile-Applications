package com.example.swapsense.ui.dashboard

import android.Manifest
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.swapsense.databinding.FragmentDashboardBinding
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class DashboardFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDashboardBinding? = null

    // TextViews to display sensor data
    private val binding get() = _binding!!
    private lateinit var tvAccelerometerX: TextView
    private lateinit var tvAccelerometerY: TextView
    private lateinit var tvAccelerometerZ: TextView
    private lateinit var tvGyroscopeX: TextView
    private lateinit var tvGyroscopeY: TextView
    private lateinit var tvGyroscopeZ: TextView
    private lateinit var tvLight: TextView

    // TODO
    // Declare Request codes for permissions
    companion object {
        private const val TAG = "DashboardFragment"
    }

    // Sensor variables
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var lightSensor: Sensor? = null

    // TODO
    // Callback for the result from requesting permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            registerSensorListeners()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission is required to access sensors",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO
        // Initialize TextViews from the layout
        tvAccelerometerX = binding.tvAccelerometerX
        tvAccelerometerY = binding.tvAccelerometerY
        tvAccelerometerZ = binding.tvAccelerometerZ
        tvGyroscopeX = binding.tvGyroscopeX
        tvGyroscopeY = binding.tvGyroscopeY
        tvGyroscopeZ = binding.tvGyroscopeZ
        tvLight = binding.tvLight

        // TODO
        // Get the SensorManager instance
        // Get list of all Sensors
        // LOG it
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        Log.d(TAG, "Available sensors: ${allSensors.map { it.name }}")

        // TODO
        // Get sensor instances
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // TODO
        // Check if Sensors available
        // Toast Message if Sensor not available
        if (accelerometer == null) {
            Toast.makeText(requireContext(), "The accelerometer is not available", Toast.LENGTH_SHORT).show()
            tvAccelerometerX.text = "N/A"
            tvAccelerometerY.text = "N/A"
            tvAccelerometerZ.text = "N/A"
        }
        if (gyroscope == null) {
            Toast.makeText(requireContext(), "Gyroscope not available", Toast.LENGTH_SHORT).show()
            tvGyroscopeX.text = "N/A"
            tvGyroscopeY.text = "N/A"
            tvGyroscopeZ.text = "N/A"
        }
        if (lightSensor == null) {
            Toast.makeText(requireContext(), "The light sensor is not available", Toast.LENGTH_SHORT).show()
            tvLight.text = "N/A"
        }

        // TODO
        // Register listeners
        checkPermission()
    }

    // TODO
    // checkPermission for the SENSORS
    private fun checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            } else {
                registerSensorListeners()
            }
        } else {
            registerSensorListeners()
        }
    }

    private fun registerSensorListeners() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Accelerometer registered")
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Gyroscope registered")
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Light sensor registered")
        }
    }

    // TODO
    // Define SensorEventListeners
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                tvAccelerometerX.text = String.format("%.2f m/s²", event.values[0])
                tvAccelerometerY.text = String.format("%.2f m/s²", event.values[1])
                tvAccelerometerZ.text = String.format("%.2f m/s²", event.values[2])
            }
            Sensor.TYPE_GYROSCOPE -> {
                tvGyroscopeX.text = String.format("%.2f rad/s", event.values[0])
                tvGyroscopeY.text = String.format("%.2f rad/s", event.values[1])
                tvGyroscopeZ.text = String.format("%.2f rad/s", event.values[2])
            }
            Sensor.TYPE_LIGHT -> {
                tvLight.text = String.format("%.1f lux", event.values[0])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 精度变化时不需要处理
    }

    override fun onResume() {
        super.onResume()
        registerSensorListeners()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}