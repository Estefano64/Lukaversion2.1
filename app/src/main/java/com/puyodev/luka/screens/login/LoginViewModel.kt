package com.puyodev.luka.screens.login

import androidx.compose.runtime.mutableStateOf
import com.puyodev.luka.LOGIN_SCREEN
import com.puyodev.luka.SIGNUP_SCREEN
import com.puyodev.luka.R.string as AppText
import com.puyodev.luka.PAY_SCREEN
import com.puyodev.luka.common.ext.isValidEmail
import com.puyodev.luka.common.snackbar.SnackbarManager
import com.puyodev.luka.model.service.AccountService
import com.puyodev.luka.model.service.LogService
import com.puyodev.luka.screens.LukaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.content.Intent
import android.content.IntentSender
import androidx.activity.ComponentActivity
import com.google.firebase.auth.OAuthProvider
import com.puyodev.luka.di.GoogleAuthHelper
import com.puyodev.luka.model.service.ConfigurationService
import com.puyodev.luka.PHONE_AUTH_SCREEN
import kotlinx.coroutines.tasks.await
import com.facebook.AccessToken

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val accountService: AccountService,
  private val configurationService: ConfigurationService,
  private val googleAuthHelper: GoogleAuthHelper,
  logService: LogService
) : LukaViewModel(logService) {
  var uiState = mutableStateOf(LoginUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    if (password.isBlank()) {
      SnackbarManager.showMessage(AppText.empty_password_error)
      return
    }

    launchCatching {
      accountService.authenticate(email, password)
      // Si es exitoso, navega a SettingsScreen y elimina LoginScreen de la pila
      openAndPopUp(PAY_SCREEN, LOGIN_SCREEN)
    }
  }

  fun onForgotPasswordClick() {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackbarManager.showMessage(AppText.recovery_email_sent)
    }
  }

  fun onCreateAccountClick(openAndPopUp: (String, String) -> Unit) {
    // Si es exitoso, navega a SignUpScreen y elimina LoginScreen de la pila
    openAndPopUp(SIGNUP_SCREEN, LOGIN_SCREEN)
  }

  fun onPhoneAuthClick(openAndPopUp: (String, String) -> Unit) {
    // Navegar a la pantalla de autenticación por teléfono
    openAndPopUp(PHONE_AUTH_SCREEN, LOGIN_SCREEN)
  }

  // Nueva función para iniciar el proceso de Google Sign-In
  suspend fun startGoogleSignIn(): IntentSender? {
    return try {
      googleAuthHelper.signIn()
    } catch (e: Exception) {
      SnackbarManager.showMessage(AppText.generic_error)
      null
    }
  }

  // Nueva función para procesar el resultado del intent de Google Sign-In
  fun handleGoogleSignInResult(intent: Intent?, openAndPopUp: (String, String) -> Unit) {
    if (intent == null) {
      SnackbarManager.showMessage(AppText.generic_error)
      return
    }

    launchCatching {
      val idToken = googleAuthHelper.getGoogleIdToken(intent)
      if (idToken != null) {
        val success = accountService.signInWithGoogle(idToken)
        if (success) {
          openAndPopUp(PAY_SCREEN, LOGIN_SCREEN)
        } else {
          throw Exception("Error en la autenticación con Google")
        }
      } else {
        SnackbarManager.showMessage(AppText.generic_error)
      }
    }
  }
  
  // Método para manejar la autenticación con Facebook
  fun handleFacebookSignInResult(token: AccessToken?, openAndPopUp: (String, String) -> Unit) {
    if (token == null) {
      SnackbarManager.showMessage(AppText.generic_error)
      return
    }
    
    launchCatching {
      // Mostrar un mensaje de carga
      SnackbarManager.showMessage(AppText.loading_message)
      
      val success = accountService.signInWithFacebook(token)
      if (success) {
        openAndPopUp(PAY_SCREEN, LOGIN_SCREEN)
      } else {
        SnackbarManager.showMessage(AppText.generic_error)
      }
    }
  }
}
