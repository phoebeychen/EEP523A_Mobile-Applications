# SwapSense – EEP 523 Homework Assignment 2

## App Overview

SwapSense is an Android application built with Kotlin that demonstrates native hardware access and multi-tab navigation. The app features three core tabs accessible via a bottom navigation bar.

---

## How the App Works

### Sensors Tab
Displays real-time data from three physical sensors on the device:
- **Accelerometer** – measures the device's acceleration along the X, Y, and Z axes (m/s²)
- **Gyroscope** – measures the device's rate of rotation along the X, Y, and Z axes (rad/s)
- **Light Sensor** – measures the ambient light level in lux

The app requests the `ACTIVITY_RECOGNITION` permission on Android 10+ before registering sensor listeners. If any sensor is unavailable on the device, a Toast message is displayed and the corresponding field shows "Unavailable." Sensor listeners are registered in `onResume()` and unregistered in `onPause()` to conserve battery.

### Draw Tab
Allows users to load an image from their photo gallery and draw over it with a finger (or mouse in the emulator):
- **Select Image** – opens the photo gallery (requests `READ_MEDIA_IMAGES` on Android 13+, or `READ_EXTERNAL_STORAGE` on older versions); handles EXIF orientation so images display upright
- **Color Picker** – switch brush color between Black, Red, Blue, and Green
- **Reset** – reverts the canvas back to the original unedited image
- **Save** – compresses the edited image as JPEG and saves it to the device's Pictures/SwapSense folder via MediaStore; a Toast confirms success

The drawing surface is implemented as a custom `DrawingImageView` that converts screen touch coordinates to bitmap coordinates, so drawing stays accurate regardless of image scale or centering.

### Camera Tab
Provides a live camera preview and photo capture using CameraX:
- **Switch Camera** – toggles between the front and rear camera
- **Capture** – takes a photo and saves it to Pictures/SwapSense in the gallery; a Toast confirms success
- Requests `CAMERA` and media permissions at runtime, with appropriate handling for Android 10+ (no `WRITE_EXTERNAL_STORAGE` needed) and older versions
- Gracefully handles error scenarios (camera unavailable, permission denied) with Toast messages

---

## Time Spent

Approximately **15–20 hours** total, including:
- Initial project setup and navigation scaffolding (~2 hours)
- Sensors Tab implementation (~2 hours)
- Draw Tab – custom View, EXIF handling, MediaStore save (~5 hours)
- Camera Tab – CameraX setup, permission handling, lifecycle management (~5 hours)
- Debugging, testing on emulator, and documentation (~3 hours)

---

## Challenges

- **CameraX lifecycle management** – binding use cases to `viewLifecycleOwner` correctly, and ensuring `unbindAll()` is called before rebinding when switching cameras
- **Custom DrawingImageView coordinate mapping** – converting raw touch event coordinates to bitmap-space coordinates using scale and offset values computed in `onDraw()`
- **EXIF orientation** – images from the gallery often arrive rotated; reading the `ExifInterface` and applying a `Matrix` rotation was necessary to display them correctly
- **Multi-version permission handling** – `READ_MEDIA_IMAGES` (API 33+), `READ_EXTERNAL_STORAGE` (API ≤ 32), `WRITE_EXTERNAL_STORAGE` (API ≤ 29), and `ACTIVITY_RECOGNITION` (API 29+) all required separate handling
- **Navigation component setup** – wiring `NavHostFragment`, `mobile_navigation.xml`, and `BottomNavigationView` together so that fragment IDs matched across all three files

---

## AI Usage

This assignment was developed with assistance from **Claude (Anthropic)** as an AI coding assistant. Claude was used to:
- Scaffold boilerplate code for Fragment classes and XML layouts
- Fill in TODO placeholders in the template codebase
- Debug crash logs (e.g., `NavController not set`, `Unresolved reference`)
- Explain Android concepts (Navigation component, CameraX, SensorManager)

All generated code was reviewed, understood, and adapted by the developer before use.

---

## References

- [Android CameraX Documentation](https://developer.android.com/training/camerax)
- [Android SensorManager Documentation](https://developer.android.com/reference/android/hardware/SensorManager)
- [Android Navigation Component](https://developer.android.com/guide/navigation)
- [MediaStore API](https://developer.android.com/reference/android/provider/MediaStore)
- [ExifInterface](https://developer.android.com/reference/androidx/exifinterface/media/ExifInterface)
- [SVG Icons – svgrepo.com](https://www.svgrepo.com)
- Stack Overflow – various threads on CameraX permission handling and custom View touch events
- Claude (Anthropic) – AI assistant used for code generation and debugging

---

## Project Structure

```
com.example.swapsense
├── MainActivity.kt               # Bottom navigation + NavController setup
├── ui/
│   ├── dashboard/
│   │   └── DashboardFragment.kt  # Sensors Tab
│   ├── DrawingImage/
│   │   ├── DrawingImageFragment.kt  # Draw Tab
│   │   └── DrawingImageView.kt      # Custom drawing canvas
│   └── Camera/
│       └── CameraFragment.kt     # Camera Tab
res/
├── layout/
│   ├── activity_main.xml
│   ├── fragment_dashboard.xml
│   ├── fragment_draw.xml
│   └── fragment_camera.xml
├── menu/
│   └── bottom_nav_menu.xml
└── navigation/
    └── mobile_navigation.xml
```