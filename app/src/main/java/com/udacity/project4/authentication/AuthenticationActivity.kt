package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AuthenticationActivity"
        const val SIGN_IN_RESULT_CODE = 1001
    }
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }
    var currentUser = FirebaseAuth.getInstance().currentUser
    private val authenticationState = if (currentUser != null)
    {
        AuthenticationState.AUTHENTICATED
    }
    else
    {
        AuthenticationState.UNAUTHENTICATED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (authenticationState == AuthenticationState.AUTHENTICATED && currentUser != null)
        {
            Log.i(TAG,"Already Authenticated")
           navigateToReminderActivity()
        }
        else
        {
            Log.i(TAG,"Unauthenticated"+ currentUser?.displayName.toString() + authenticationState.toString())

        }
        var btn = findViewById<Button>(R.id.loginin_or_registerButton)
        btn.setOnClickListener {
            launchSignInFlow()
        }
        // a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
    }
    private fun navigateToReminderActivity()
    {
        var authIntent = Intent(this,RemindersActivity::class.java)
        startActivity(authIntent)
        this.finish()
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                navigateToReminderActivity()
                // User successfully signed in
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                this.finish()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(
                    TAG,
                    "Sign in unsuccessful ${response?.error?.errorCode}"
                )
            }
        }
    }
}
