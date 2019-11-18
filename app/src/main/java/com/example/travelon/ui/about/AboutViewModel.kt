package com.example.travelon.ui.about

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AboutViewModel : ViewModel() {
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
