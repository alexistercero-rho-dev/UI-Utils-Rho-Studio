package com.rho.studio.ui.viewmodel

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.rho.studio.ui.BR
import com.rho.studio.ui.data.model.Credentials

class LoginViewModel : BaseObservable() {
    private val credentialslive: Credentials = Credentials("", "")

    private val successMessage = "Login successful"
    private val errorMessage = "Email or Password is not valid"

    @get:Bindable
    var toastMessage: String? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.toastMessage)
        }

    // Getter and setter methods for email variable
    @get:Bindable
    var userEmail: String?
        get() = credentialslive.email                       // 1. Reads from the Credentials object
        set(value) {
            credentialslive.email = value                   // 2. Updates the Credentials object
            notifyPropertyChanged(BR.userEmail)     // 3. Tells the UI to refresh
        }

    // Getter and setter methods for password variable
    @get:Bindable
    var userPassword: String?
        get() = credentialslive.password
        set(value) {
            credentialslive.password = value
            notifyPropertyChanged(BR.userPassword)
        }

    // Actions to be performed when the user clicks the LOGIN button
    fun onButtonClicked() {
        toastMessage = if (isValid()) successMessage else errorMessage
    }
    // Method to ensure fields are not empty and email/password validation
    private fun isValid(): Boolean {
        return !userEmail.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(userEmail!!).matches()
                && !userPassword.isNullOrEmpty() && userPassword!!.length > 5
    }
}