package com.example.travelon.data

import com.example.travelon.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    fun firebaseLogin(username: String, password: String, completionHandler: (Result<FirebaseUser>) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    completionHandler(Result.Error(IOException("Error logging in")))
                    return@addOnCompleteListener
                } else {
                    val user = it.result?.user ?: return@addOnCompleteListener

                    completionHandler(Result.Success(user))
                }
            }
    }

    fun firebaseRegister(username: String, password: String, completionHandler: (Result<FirebaseUser>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    completionHandler(Result.Error(IOException("Error logging in")))
                    return@addOnCompleteListener
                } else {
                    val user = it.result?.user ?: return@addOnCompleteListener

                    completionHandler(Result.Success(user))
                }
            }
    }
}

