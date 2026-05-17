# Reflection

### What features or sensors did your app use?
*   **GPS Location Sensor**: The app utilizes the device's GPS via the `FusedLocationProviderClient` to fetch current latitude and longitude for weather updates.
*   **OpenWeatherMap API**: Integrated RESTful networking using Retrofit to fetch real-time weather data.
*   **State Management**: Used `ViewModel` and `StateFlow` to manage UI states (Loading, Success, Error).
*   **Local Search History**: Implemented a persistent list of recent searches that tracks and displays city names and temperature ranges.
*   **Jetpack Compose**: Developed a modern, declarative UI using Material 3 and dynamic layout components.

### How did Gemini help you?
*   **Architecture Guidance**: Gemini helped structure the MVVM pattern and transition from a multi-activity design to a single-activity state-based navigation system.
*   **Complex Bug Resolution**: It provided critical insights into handling UTC offsets for timezone-accurate sunrise and sunset times.
*   **Boilerplate Generation**: It accelerated development by generating initial Retrofit interfaces and basic Compose UI templates.

### What errors, weaknesses, or missing pieces did you find in Gemini’s output?
*   **Lack of Specificity in Models**: Gemini's initial weather data models omitted the `timezone` field, which was necessary for correct time display.
*   **Requirement Compliance**: It often suggested standard "Network Error" strings instead of the strict, assignment-specific messages required (e.g., "Please connect to internet").
*   **Compose Nuances**: The AI occasionally struggled with advanced Compose focus management, such as correctly triggering the keyboard using `FocusRequester` within a `LaunchedEffect`.

### What did you change or fix?
*   **Data Model Enrichment**: Manually added the `timezone` field to the `WeatherResponse` class.
*   **Date Logic Correction**: Rewrote the time conversion logic to use UTC as a base and add the seconds-offset provided by the API, ensuring accuracy for any global city.
*   **Custom Exception Handling**: Refined the `catch` blocks in the ViewModel to distinguish between `IOException` (network) and `HttpException` (invalid city), returning the exact required strings.

### What did you learn about using AI in software development?
*   **AI is an Assistant, Not an Author**: While AI is great for speed, it requires constant human verification to meet precise technical specifications.
*   **Prompt Precision**: I learned that being extremely specific about constraints and expected error messages saves significant time in the long run.
*   **Iterative Debugging**: Software development with AI is a conversation. Fixing one piece of code often reveals edge cases that require further iterative prompts to resolve completely.
