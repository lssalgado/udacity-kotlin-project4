package com.udacity.project4.authentication

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*

private const val SIGN_IN_RESULT_CODE = 1111

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        viewModel.authenticationState.observe(this, Observer { state ->
            if (state == FirebaseUserLiveData.AuthenticationState.AUTHENTICATED) {
                RemindersActivity.start(this)
            } else {
                loginButton.visibility = View.VISIBLE
                loginButton.setOnClickListener {
                    launchSignInFlow()
                }
            }
        })
    }

    // Extracted from:
    // https://github.com/udacity/android-kotlin-login/blob/master/app/src/main/java/com/example/android/firebaseui_login_sample/LoginFragment.kt#L71
    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val customLayout =
            AuthMethodPickerLayout.Builder(R.layout.login_layout).setEmailButtonId(R.id.emailButton)
                .setGoogleButtonId(R.id.googleButton).build()

        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(customLayout)
                .build(), SIGN_IN_RESULT_CODE
        )
    }
}
