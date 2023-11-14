package com.example.fitness_app.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isAuthenticated = MutableLiveData<Boolean>()
    private val database = FirebaseDatabase.getInstance().reference
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated
    init{
        _isAuthenticated.value = false
    }
    fun signUp(userName: String, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                    _isAuthenticated.postValue(true)
//                    updateUserUsername(userName)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
//    private fun updateUserUsername(userName: String) {
//        database.child("users").child(auth.currentUser!!.uid).child("username").setValue(userName)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("uName", "Successfully added username")
//                } else {
//                    Log.d("uName", "UnSuccessfully added username")
//                }
//            }
//    }
}

