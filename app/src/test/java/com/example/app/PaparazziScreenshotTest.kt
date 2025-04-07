package com.example.app

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import bg.zahov.app.ui.authentication.login.LoginContent
import bg.zahov.app.ui.authentication.signup.SignupContent
import org.junit.Rule
import org.junit.Test

class PaparazziScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @Test
    fun loginScreen(){
        paparazzi.snapshot {
            LoginContent(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                passwordVisibility = false,
                onPasswordVisibilityChange = {},
                navigateSignUp = {},
                logIn = {},
                resetPassword = {}
            )
        }
    }

    @Test
    fun signInTest() {
        paparazzi.snapshot {
            SignupContent(
                username = "",
                email = "",
                password = "",
                showPassword = false,
                confirmPassword = "",
                onNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onSignupButtonPressed = {},
                onPasswordVisibilityChange = {},
                onNavigateToLogin = {}
            )
        }
    }

}