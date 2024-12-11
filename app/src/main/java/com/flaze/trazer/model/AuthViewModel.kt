package com.flaze.trazer.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    private val authState: StateFlow<AuthState> = _authState

    // Using isLoggedIn to observe authentication state
    val isLoggedIn: StateFlow<Boolean> = authState.map {
        it is AuthState.Authenticated
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = FirebaseAuth.getInstance().currentUser != null
    )

    init {
        // Listen for Firebase Authentication state changes
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser != null) {
                _authState.value = AuthState.Authenticated(auth.currentUser!!)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = withContext(Dispatchers.IO) {
            Firebase.auth.signInWithCredential(credential)
        }
        // Handle result and update auth state
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        val result = withContext(Dispatchers.IO) {
            Firebase.auth.signInWithEmailAndPassword(email, password)
        }
        // Handle result and update auth state
    }

    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            Firebase.auth.signOut()
        }
    }
}