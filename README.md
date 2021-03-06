# Location Reminder

A Todo list app with location reminders that remind the user to do something when he reaches a specific location. The app will require the user to create an account and login to set and access reminders.

## Getting Started

1. Clone the project to your local machine.
2. Open the project using Android Studio.
3. Add a variable with name MAPS_API_KEY and your Google Maps API Key as the value in local.properties!!  
  3.1 `MAPS_API_KEY=<your key>`

### Dependencies

```
1. A created project on Firebase console.
2. A create a project on Google console.
```

### Installation

Step by step explanation of how to get a dev environment running.

- [x] To enable Firebase Authentication:
        - [x] Go to the authentication tab at the Firebase console and enable Email/Password and Google Sign-in methods.
        - [x] download `google-services.json` and add it to the app.
- [x] To enable Google Maps:
    - [x] Go to APIs & Services at the Google console.
    - [x] Select your project and go to APIs & Credentials.
    - [x] Create a new api key and restrict it for android apps.
    - [x] Add your package name and SHA-1 signing-certificate fingerprint.
    - [x] Enable Maps SDK for Android from API restrictions and Save.
    - [ ] **Add a variable with name MAPS_API_KEY and your Google Maps API Key as the value in local.properties!!**
- [x] Run the app on your mobile phone or emulator with Google Play Services in it.

## Project Instructions
- [x] Create a Login screen to ask users to login using an email address or a Google account.  Upon successful login, navigate the user to the Reminders screen.   If there is no account, the app should navigate to a Register screen.
- [x] Create a Register screen to allow a user to register using an email address or a Google account.
- [x] Create a screen that displays the reminders retrieved from local storage. If there are no reminders, display a   "No Data"  indicator.  If there are any errors, display an error message.
- [x] Create a screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder.
- [x] Create a screen to add a reminder when a user reaches the selected location.  Each reminder should include
    - [x] title
    - [x] description
    - [x] selected location
- [x] Reminder data should be saved to local storage.
- [x] For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area.
- [x] Provide testing for the ViewModels, Coroutines and LiveData objects.
- [x] Create a FakeDataSource to replace the Data Layer and test the app in isolation.
- [x] Use Espresso and Mockito to test each screen of the app:
    - [x] Test DAO (Data Access Object) and Repository classes.
    - [x] Add testing for the error messages.
    - [x] Add End-To-End testing for the Fragments navigation.

## Rubric
- [x] **User Authentication**
  - [x] Create Login and Registration screens
    - [x] Login screen allows users to login using email or a Google Account
    - [x] If the user does not exist, the app navigates to a Registration screen
  - [x] Enable user accounts using Firebase Authentication and Firebase UI.
    - [x] The project includes a FirebaseUI dependency
    - [x] Authentication is enabled through the Firebase console.
- [x] **Map View**
  - [x] Create a Map view that shows the user's current location
    - [x] A screen that shows a map and asks the user to allow the location permission to show his location on the map.
    - [x] The app works on all the different Android versions including Android Q.
  - [x] Add functionality to allow the user to select POIs to set reminders
    - [x] The app asks the user to select a location or POI on the map and add a new marker at that location. Upon saving, the selected location is returned to the Save Reminder page and the user is asked to input the title and description for the reminder.
    - [x] When the reminder is saved, a geofencing request is created.
  - [x] Style the map
    - [x] Map Styling has been updated using the map styling wizard to generate a nice looking map.
    - [x] Users have the option to change map type.
  - [x] Display a notification when a selected POI is reached
    - [x] When the user enters a geofence, a reminder is retrieved from the local storage and a notification showing the reminder title will appear, even if the app is not open.
- [x] **Reminders**
  - [x] Add a screen to create reminders
    - [x] Reminder data includes title and description.
    - [x] The user-entered data will be captured using live data and data binding.
    - [x] RemindersLocalRepository is used to save the reminder to the local DB. And the geofencing request will be created after confirmation.
  - [x] Add a list view that displays the reminders
    - [x] All reminders in the location DB is displayed
    - [x] If the location DB is empty, a no data indicator is displayed.
    - [x] User can navigate from this screen to another screen to create a new reminder.
  - [x] Display details about a reminder when a selected POI is reached and the user clicked on the notification.
    - [x] When the user clicks a notification, when he clicks on it, a new screen appears to display the reminder details.
- [ ] **Testing**
  - [x] Use MVVM and Dependency Injection to architect your app.
    - [x] The app follows the MVVM design pattern and uses ViewModels to hold the live data objects, do the validation and interact with the data sources.
    - [x] The student retrieved the ViewModels and DataSources using Koin.
  - [x] Test the ViewModels, Coroutines, and LiveData
    - [x] RemindersListViewModelTest or SaveReminderViewModelTest are present in the test package that tests the functions inside the view model.
    - [ ] Live data objects are tested using shouldReturnError and check_loading testing functions.
  - [x] Create a FakeDataSource to replace the Data Layer and test the app in isolation.
    - [x] Project repo contains a FakeDataSource class that acts as a test double for the LocalDataSource.
  - [x] Use Espresso and Mockito to test the app UI and Fragments Navigation.
    - [x] Automation Testing using ViewMatchers and ViewInteractions to simulate user interactions with the app.
    - [x] Testing for Snackbar and Toast messages.
    - [x] Testing the fragments??? navigation.
    - [x] The testing classes are at androidTest package.
  - [x] Test DAO and Repository classes
    - [x] Testing uses Room.inMemoryDatabaseBuilder to create a Room DB instance.
      - [x] inserting and retrieving data using DAO.
      - [x] predictable errors like data not found.
- [x] **Code Quality**
  - [x] Write code using best practices for Android development with Kotlin
    - [x] Code uses meaningful variable names and method names that indicate what the method does.
- [ ] **Extra**
  - [ ] Test Coverage for the whole app.
  - [x] Update the app styling and map design using material design and map design.
  - [ ] Edit and Delete Reminders and Geofence requests.
  - [ ] Allow the user to create a shape like polygons or circles on the map to select the area.
  - [ ] Allow the user to change the reminding location range.

## Student Deliverables:

1. APK file of the final project.
2. Git Repository with the code.

## Built With

* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.

## License
