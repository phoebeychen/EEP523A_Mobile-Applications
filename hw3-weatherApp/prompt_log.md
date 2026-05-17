# Prompt Log - Weather App Development 

This document logs the interaction with the AI assistant during the development of the HW3 Weather App, specifically highlighting prompts used to identify and resolve technical bugs.


### Prompt 1: Resolving the Timezone Conversion Bug
**Context**: The application was incorrectly displaying sunrise and sunset times using the user's local phone time instead of the searched city's actual time zone.
> "I found a new issue: the sunrise and sunset times for different cities are not being converted to the corresponding time zones. They seem to be stuck in the local system time."

### Prompt 2: Fixing Navigation and Search Bar Interaction
**Context**: The search bar on the Home screen wasn't reliably triggering the transition to the Search screen, and there were focus-related bugs when navigating back.
> "Fix the navigation and focus behavior between the Home and Search screens to ensure the search bar correctly triggers the search history view and the back button returns to the main page. Address the issue where focus-related loops might prevent proper navigation."

### Prompt 3: Correcting Network Error Display (Technical vs. User-Friendly)
**Context**: When the network was disabled, the app showed raw technical logs (like "Failed to connect...") instead of the required "Please connect to internet" message.
> "I've turned off cellular data and Wi-Fi, but the app shows a technical connection error string: 'Something went wrong: Failed to connect to api.openweathermap.org...'. Please update the logic to catch these exceptions and display the specific requirement: 'Please connect to internet'."

### Prompt 4: Handling "City Not Found" and Validation Logic
**Context**: Fixing the lack of feedback when a user entered an invalid city name or left the input field blank.
> "The search component needs to handle validation bugs: If the user types a name that isn't a valid city, show 'City not found'. If they leave it blank, show 'City Name cannot be blank'. Please implement these fixes one by one."

## Conversation Summary

The development process involved several critical bug-fixing phases to ensure the app met all functional requirements:

1.  **Temporal Bug Fix**: We identified that the UTC timestamps from the API were being interpreted by the phone's local timezone. We fixed this by incorporating the `timezone` offset from the OpenWeatherMap API and using UTC-based formatting to calculate the correct local time for any city.
2.  **Navigation Stability**: Resolved a UI bug by changing the search bar from a live `TextField` (which caused focus conflicts) to a clickable `Surface`. We also used `FocusRequester` to solve the bug where the keyboard wouldn't automatically open on the search screen.
3.  **Exception Handling Robustness**: Overhauled the `WeatherViewModel`'s error handling. By specifically catching `java.io.IOException` and `HttpException`, we were able to suppress technical error logs and provide the exact user-friendly strings required for connectivity and data-not-found scenarios.
4.  **State Consistency**: Managed UI states to ensure that loading indicators and error messages appear correctly, preventing the app from showing stale data during failed network requests.
