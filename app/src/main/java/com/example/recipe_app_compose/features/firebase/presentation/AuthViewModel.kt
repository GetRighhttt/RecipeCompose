package com.example.recipe_app_compose.features.firebase.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// TODO: Update UI with ViewModel
class AuthViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
            } catch (e: Exception) {
                Log.e("Firebase", "Error signing in: $e")
            }
        }
    }
}