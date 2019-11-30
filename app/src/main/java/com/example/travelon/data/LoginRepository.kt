package com.example.travelon.data

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.travelon.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    fun firebaseLogin(username: String, password: String, completionHandler: (Result<FirebaseUser>) -> Unit) {
        dataSource.firebaseLogin(username, password) {
            if (it is Result.Success) {
                user = it.data.displayName?.let { it1 -> LoggedInUser( it.data.uid, it1) }
            }
            completionHandler(it)
        }
    }

    fun firebaseRegister(username: String, password: String, completionHandler: (Result<FirebaseUser>) -> Unit) {
        dataSource.firebaseRegister(username, password) {
            if (it is Result.Success) {
                user = it.data.displayName?.let { it1 -> LoggedInUser( it.data.uid, it1) }

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                    .build()

                it.data.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }

                completionHandler(it)
            } else {
                completionHandler(Result.Error(IOException("Error logging in")))
            }

        }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
