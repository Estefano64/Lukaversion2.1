package com.puyodev.luka.screens.phone

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puyodev.luka.R.string as AppText
import com.puyodev.luka.common.composable.BasicButton
import com.puyodev.luka.common.composable.BasicToolbar
import com.puyodev.luka.common.ext.basicButton

@Composable
fun PhoneAuthScreen(
    openAndPopUp: (String, String) -> Unit,
    viewModel: PhoneAuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val activity = LocalContext.current as Activity

    PhoneAuthScreenContent(
        uiState = uiState,
        onPhoneNumberChange = viewModel::onPhoneNumberChange,
        onVerificationCodeChange = viewModel::onVerificationCodeChange,
        onSendVerificationCodeClick = { viewModel.onSendVerificationCodeClick(activity) },
        onVerifyCodeClick = { viewModel.onVerifyCodeClick(openAndPopUp) }
    )
}

@Composable
fun PhoneAuthScreenContent(
    uiState: PhoneAuthUiState,
    onPhoneNumberChange: (String) -> Unit,
    onVerificationCodeChange: (String) -> Unit,
    onSendVerificationCodeClick: () -> Unit,
    onVerifyCodeClick: () -> Unit
) {
    BasicToolbar(AppText.login_details)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!uiState.isCodeSent) {
            // Fase 1: Ingresar número de teléfono
            Text(
                text = "Ingresa tu número de teléfono",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Número de teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onSendVerificationCodeClick() },
                modifier = Modifier.basicButton(),
                enabled = !uiState.isLoading && uiState.phoneNumber.isNotEmpty()
            ) {
                Text(text = "Continuar")
            }
            
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        } else {
            // Fase 2: Ingresar código de verificación
            Text(
                text = "Ingresa el código de verificación enviado a tu teléfono",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = uiState.verificationCode,
                onValueChange = onVerificationCodeChange,
                label = { Text("Código de verificación") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onVerifyCodeClick() },
                modifier = Modifier.basicButton(),
                enabled = !uiState.isLoading && uiState.verificationCode.isNotEmpty()
            ) {
                Text(text = "Verificar")
            }
            
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
        
        if (uiState.errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
} 