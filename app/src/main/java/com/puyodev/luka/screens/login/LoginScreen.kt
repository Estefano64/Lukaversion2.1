package com.puyodev.luka.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.puyodev.luka.R.string as AppText
import com.puyodev.luka.common.composable.*
import com.puyodev.luka.common.ext.basicButton
import com.puyodev.luka.common.ext.fieldModifier
import com.puyodev.luka.common.ext.textButton
import com.puyodev.luka.ui.theme.LukaTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
  openAndPopUp: (String, String) -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  
  // Launcher para la actividad de Google Sign-In
  val googleSignInLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    // Procesar el resultado del intent
    viewModel.handleGoogleSignInResult(result.data, openAndPopUp)
  }

  LoginScreenContent(
    uiState = uiState,
    onEmailChange = viewModel::onEmailChange,
    onPasswordChange = viewModel::onPasswordChange,
    onSignInClick = { viewModel.onSignInClick(openAndPopUp) },
    onForgotPasswordClick = viewModel::onForgotPasswordClick,
    onCreateAccountClick = { viewModel.onCreateAccountClick(openAndPopUp) },
    onGoogleSignInClick = { 
      // Iniciar el proceso de Google Sign-In
      scope.launch {
        val intentSender = viewModel.startGoogleSignIn()
        if (intentSender != null) {
          try {
            googleSignInLauncher.launch(
              androidx.activity.result.IntentSenderRequest.Builder(intentSender).build()
            )
          } catch (e: Exception) {
            // Manejar errores
          }
        }
      }
    },
    onPhoneAuthClick = { viewModel.onPhoneAuthClick(openAndPopUp) }
  )
}

@Composable
fun LoginScreenContent(
  modifier: Modifier = Modifier,
  uiState: LoginUiState,
  onEmailChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
  onSignInClick: () -> Unit,
  onForgotPasswordClick: () -> Unit,
  onCreateAccountClick: () -> Unit,
  onGoogleSignInClick: () -> Unit,
  onPhoneAuthClick: () -> Unit
) {
  BasicToolbar(AppText.login_details)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    EmailField(
      uiState.email,
      onEmailChange,
      Modifier.fieldModifier("email_field")
    )

    PasswordField(
      uiState.password,
      onPasswordChange,
      Modifier.fieldModifier("password_field")
    )
    
    BasicButton(AppText.sign_in, Modifier.basicButton()) { onSignInClick() }

    BasicTextButton(AppText.forgot_password, Modifier.textButton()) {
      onForgotPasswordClick()
    }
    
    BasicTextButton(AppText.start_to_create_account, Modifier.textButton()) {
      onCreateAccountClick()
    }
    
    // Agregar botón de Google después del botón de inicio de sesión normal
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
      text = "O inicia sesión con",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
      onClick = { onGoogleSignInClick() },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(
        containerColor = Color.White
      )
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        // Aquí puedes usar un icono de Google si lo tienes en tus recursos
        // Si no, puedes añadir el icono como recurso o usar un texto con estilo
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Continuar con Google",
          color = Color.Black,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
    
    // Agregar botón para autenticación por teléfono
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(
      onClick = { onPhoneAuthClick() },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
      )
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Continuar con teléfono",
          color = Color.White,
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
  val uiState = LoginUiState(
    email = "correo123@gmail.com"
  )

  LukaTheme {
    LoginScreenContent(
      uiState = uiState,
      onEmailChange = { },
      onPasswordChange = { },
      onSignInClick = { },
      onForgotPasswordClick = { },
      onCreateAccountClick = { },
      onGoogleSignInClick = { },
      onPhoneAuthClick = { }
    )
  }
}
