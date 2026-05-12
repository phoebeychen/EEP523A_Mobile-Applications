# Prompt Log – EEP 523 Homework Assignment 2
## AI Tool Used: Claude

---

## Selected Prompts

---

### Prompt 1 – XML Layout Error

**Prompt:**
> My `activity_main.xml` has two errors: "Tag start is not closed :18" and "Unexpected text found in layout file: '//TODO: Add Navigation Fragment />' :19". What is causing this and how do I fix it?

**Summary of AI Response:**
The AI explained that the template had left a plain-text comment (`//TODO: Add Navigation Fragment />`) inside an XML file, which is invalid XML syntax. XML only accepts `<!-- comment -->` style comments, so the parser was failing to close the tag on line 18. The fix was to replace the TODO comment with a proper `FragmentContainerView` element referencing the NavHostFragment.

**What I changed:**
Replaced the invalid TODO text with a correctly structured `<androidx.fragment.app.FragmentContainerView>` block pointing to `@navigation/mobile_navigation`.

---

### Prompt 2 – App Crashes on Launch (NavController Error)

**Prompt:**
> My app crashes immediately on launch with this error: `java.lang.IllegalStateException: Activity does not have a NavController set on 2131231041`. Here is my `MainActivity.kt`. What is wrong?

**Summary of AI Response:**
The AI identified that `findNavController(R.id.nav_host_fragment)` was being called too early in `onCreate()`, before the `NavHostFragment` had finished attaching its `NavController`. The correct approach is to retrieve the `NavHostFragment` instance from `supportFragmentManager` first, then access its `navController` property directly.

**What I changed:**
Replaced:
```kotlin
val navController = findNavController(R.id.nav_host_fragment)
```
With:
```kotlin
val navHostFragment = supportFragmentManager
    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
val navController = navHostFragment.navController
```

---

### Prompt 3 – Unresolved Reference After Adding Dependency

**Prompt:**
> I added `androidx.exifinterface:exifinterface:1.3.6` to my `build.gradle.kts` and clicked Sync Now, but I'm still getting `Unresolved reference: ExifInterface` in `DrawingImageFragment.kt`. Why?

**Summary of AI Response:**
The AI pointed out that the dependency was correctly added but the import statement was missing from the Kotlin file. The class `ExifInterface` exists in the `androidx.exifinterface.media` package and must be explicitly imported. Gradle syncing only makes the library available to the project; the individual file still needs the correct import statement.

**What I changed:**
Added the following import to `DrawingImageFragment.kt`:
```text
import androidx.exifinterface.media.ExifInterface
```

---

### Prompt 4 – App Installs But Does Not Launch

**Prompt:**
> My app builds successfully but never appears on the emulator. The build log only shows `assembleDebug UP-TO-DATE` with no `installDebug` step. Running `adb shell am start` gives `Error type 3: Activity class does not exist`. What is wrong?

**Summary of AI Response:**
The AI asked to see `AndroidManifest.xml` and immediately identified that the `<activity>` declaration for `MainActivity` was missing — only a TODO comment remained in its place. Without an `<activity>` entry with an `<intent-filter>` containing `MAIN` and `LAUNCHER`, Android cannot launch the app. The `assembleDebug` task still succeeds because compilation does not require a launchable activity, which is why the failure was silent.

**What I changed:**
Added the following inside `<application>` in `AndroidManifest.xml`:
```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.SwapSense">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

---

### Prompt 5 – Redeclaration Error for DashboardFragment

**Prompt:**
> I'm getting `Redeclaration: class DashboardFragment : Fragment, SensorEventListener`. I only see one class declaration in `DashboardFragment.kt`. What could cause this?

**Summary of AI Response:**
The AI suggested using global search (Shift+Shift) to find all occurrences of `DashboardFragment` across the project. It explained that this error means the same class name is declared in two files within the same package — a situation that commonly occurs when a manually created file is accidentally given the wrong package name and class name.

**What I changed:**
Found that `SensorsFragment.kt` (created manually earlier) had accidentally been given the package `com.example.swapsense.ui.dashboard` and class name `DashboardFragment`, duplicating the real `DashboardFragment.kt`. Deleted `SensorsFragment.kt` to resolve the conflict.

---

### Prompt 6 – Bottom Navigation Bar Not Visible

**Prompt:**
> The app launches and shows the Sensors label at the top, but the bottom navigation bar is completely invisible. The `activity_main.xml` layout looks correct to me. What could be missing?

**Summary of AI Response:**
The AI reviewed `activity_main.xml` and identified that the `BottomNavigationView` was missing the `app:menu` attribute. Without this attribute the view renders with zero height because it has no menu items to display, making it invisible even though it exists in the layout.

**What I changed:**
Added `app:menu="@menu/bottom_nav_menu"` to the `BottomNavigationView` in `activity_main.xml`.

---

## Conversation Summary

Throughout this assignment, Claude was used primarily as a **debugging assistant**. The most common issues fell into three categories:

**1. Template placeholders causing build or runtime errors**
Several TODO comments in the template were left as plain text in contexts where they caused syntax errors or missing configurations (invalid XML, missing Manifest entry). The AI was effective at identifying these quickly from error messages alone.

**2. Android lifecycle and navigation timing issues**
The `NavController` crash was a subtle timing bug. The AI correctly diagnosed it from the stack trace and explained why accessing `NavController` through `NavHostFragment` instead of `findNavController()` fixes the issue.

**3. Silent failures**
The most time-consuming issue was the app installing but not launching, because `assembleDebug` succeeds even without a launchable activity. Standard Android Studio run buttons also gave no useful feedback. The AI correctly pointed to the missing `<activity>` declaration after seeing the `adb` error output.

**Limitations observed:**
- The AI occasionally provided code with function signatures that did not match the template (e.g., `saveImageToUri(uri)` vs `saveImageToUri(uri, bitmap)`), requiring manual review and adaptation.
- Some generated code assumed project structure that differed from the actual template layout, requiring adjustments to package names and file paths.
- The AI could not directly access the emulator or run the code itself, so all suggestions required manual testing to verify.