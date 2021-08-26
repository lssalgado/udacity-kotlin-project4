# Location Reminder

A Todo list app with location reminders that remind the user to do something when he reaches a specific location. The app will require the user to create an account and login to set and access reminders.

## Getting Started

1. Clone the project to your local machine.
2. Open the project using Android Studio.

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
    - [x] Copy the api key to the `google_maps_api.xml`
- [x] Run the app on your mobile phone or emulator with Google Play Services in it.

## Testing

Right click on the `test` or `androidTest` packages and select Run Tests

### Break Down Tests

Explain what each test does and why

```
1.androidTest
        //TODO: Students explain their testing here.
2. test
        //TODO: Students explain their testing here.
```

## Project Instructions
- [x] Create a Login screen to ask users to login using an email address or a Google account.  Upon successful login, navigate the user to the Reminders screen.   If there is no account, the app should navigate to a Register screen.
- [x] Create a Register screen to allow a user to register using an email address or a Google account.
- [ ] Create a screen that displays the reminders retrieved from local storage. If there are no reminders, display a   "No Data"  indicator.  If there are any errors, display an error message.
- [ ] Create a screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder.
- [ ] Create a screen to add a reminder when a user reaches the selected location.  Each reminder should include
    - [ ] title
    - [ ] description
    - [ ] selected location
- [ ] Reminder data should be saved to local storage.
- [ ] For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area.
- [ ] Provide testing for the ViewModels, Coroutines and LiveData objects.
- [ ] Create a FakeDataSource to replace the Data Layer and test the app in isolation.
- [ ] Use Espresso and Mockito to test each screen of the app:
    - [ ] Test DAO (Data Access Object) and Repository classes.
    - [ ] Add testing for the error messages.
    - [ ] Add End-To-End testing for the Fragments navigation.


## Student Deliverables:

1. APK file of the final project.
2. Git Repository with the code.

## Built With

* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.

## License
