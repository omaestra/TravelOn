package com.example.travelon.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.R
import com.example.travelon.data.LoginRepository
import com.example.travelon.data.Result

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun firebaseRegister(username: String, password: String) {

        loginRepository.firebaseRegister(username, password) { result ->
            if (result is Result.Success) {
                val user = result.data

                _registerResult.value =
                    RegisterResult(success = user.email?.let { it1 -> LoggedInUserView(it1) })
            } else {
                _registerResult.value = RegisterResult(error = R.string.login_failed)
            }
        }
    }

    fun registerDataChanged(username: String, password: String, confirmPassword: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (!passwordMatches(password, confirmPassword)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.passwords_does_not_match)
        }  else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun passwordMatches(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}