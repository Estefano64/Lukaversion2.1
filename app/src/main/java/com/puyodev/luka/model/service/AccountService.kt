/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.puyodev.luka.model.service

import com.puyodev.luka.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
  val currentUserId: String
  val hasUser: Boolean

  val currentUser: Flow<User>

  suspend fun createAccount(email: String, password: String,name:String) // Nuevo método
  suspend fun authenticate(email: String, password: String)
  suspend fun sendRecoveryEmail(email: String)
  suspend fun deleteAccount()
  suspend fun signOut()
  suspend fun signInWithGoogle(idToken: String): Boolean
  
  // Método para enviar el código de verificación al teléfono
  suspend fun sendPhoneVerification(phoneNumber: String, activity: android.app.Activity, callback: (String?, Exception?) -> Unit)
  
  // Método para verificar el código enviado al teléfono
  suspend fun verifyPhoneCode(verificationId: String, code: String): Boolean
}
