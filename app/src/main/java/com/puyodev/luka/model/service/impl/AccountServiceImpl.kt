package com.puyodev.luka.model.service.impl

import com.puyodev.luka.model.service.AccountService
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.puyodev.luka.model.User
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.app.Activity
import com.facebook.AccessToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.FirebaseException
import androidx.activity.ComponentActivity

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) : AccountService {

  override val currentUserId: String
    get() = auth.currentUser?.uid.orEmpty()

  override val hasUser: Boolean
    get() = auth.currentUser != null


  override val currentUser: Flow<User>
    get() = callbackFlow {
      val listener =
        FirebaseAuth.AuthStateListener { auth ->
          this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous.toString()) } ?: User())
        }
      auth.addAuthStateListener(listener)
      awaitClose { auth.removeAuthStateListener(listener) }
    }


  // Crear una nueva cuenta
  override suspend fun createAccount(email: String, password: String, name:String) {
    val result = auth.createUserWithEmailAndPassword(email, password).await()

    // Obtener el UID del usuario creado
    val uid = result.user?.uid ?: throw Exception("Error creando usuario")

    // Crear el documento en Firestore con datos adicionales
    val userData = hashMapOf(
      "username" to name,
      "lukitas" to 30 // Monto inicial
    )
    firestore.collection("usuarios").document(uid).set(userData).await()

    auth.signInWithEmailAndPassword(email, password).await()
  }

  // Iniciar sesión con usuario existente
  override suspend fun authenticate(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }

  override suspend fun sendRecoveryEmail(email: String) {
    auth.sendPasswordResetEmail(email).await()
  }

  override suspend fun deleteAccount() {
    val user = auth.currentUser
    if (user != null) {
      user.delete().await()
    } else {
      throw Exception("No se encontró el usuario actual para eliminar")
    }
  }

  override suspend fun signOut() {
    if (auth.currentUser!!.isAnonymous) {
      auth.currentUser!!.delete()
    }
    auth.signOut()
  }

  override suspend fun signInWithGoogle(idToken: String): Boolean {
    try {
      val credential = GoogleAuthProvider.getCredential(idToken, null)
      val result = auth.signInWithCredential(credential).await()
      
      // Verificar si es un usuario nuevo
      val isNewUser = result.additionalUserInfo?.isNewUser ?: false
      
      // Si es un usuario nuevo, crear el documento en Firestore
      if (isNewUser) {
        val uid = result.user?.uid ?: throw Exception("Error creando usuario con Google")
        val userData = hashMapOf(
          "username" to (result.user?.displayName ?: "Usuario"),
          "lukitas" to 30 // Monto inicial
        )
        
        firestore.collection("usuarios").document(uid).set(userData).await()
      }
      
      return true
    } catch (e: Exception) {
      return false
    }
  }
  
  // Implementación de la autenticación con Facebook
  override suspend fun signInWithFacebook(token: AccessToken): Boolean {
    return try {
      val credential = FacebookAuthProvider.getCredential(token.token)
      val result = auth.signInWithCredential(credential).await()
      
      // Verificar si es un usuario nuevo
      val isNewUser = result.additionalUserInfo?.isNewUser ?: false
      
      // Si es un usuario nuevo, crear el documento en Firestore
      if (isNewUser) {
        val uid = result.user?.uid ?: throw Exception("Error creando usuario con Facebook")
        val userData = hashMapOf(
          "username" to (result.user?.displayName ?: "Usuario"),
          "lukitas" to 30, // Monto inicial
          "email" to result.user?.email
        )
        
        firestore.collection("usuarios").document(uid).set(userData).await()
      }
      
      true
    } catch (e: Exception) {
      android.util.Log.e("FacebookAuth", "Error en autenticación con Facebook", e)
      false
    }
  }
  
  // Implementación de autenticación por teléfono
  override suspend fun sendPhoneVerification(
    phoneNumber: String,
    activity: Activity,
    callback: (String?, Exception?) -> Unit
  ) {
    val options = PhoneAuthOptions.newBuilder(auth)
      .setPhoneNumber(phoneNumber)
      .setTimeout(60L, TimeUnit.SECONDS)
      .setActivity(activity)
      .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
          // La verificación se completó automáticamente
          signInWithPhoneCredential(credential) { success, exception ->
            if (success) {
              callback(null, null)
            } else {
              callback(null, exception)
            }
          }
        }

        override fun onVerificationFailed(e: FirebaseException) {
          // La verificación falló
          callback(null, e)
        }

        override fun onCodeSent(
          verificationId: String,
          token: PhoneAuthProvider.ForceResendingToken
        ) {
          // El código fue enviado
          callback(verificationId, null)
        }
      })
      .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
  }

  override suspend fun verifyPhoneCode(verificationId: String, code: String): Boolean {
    return try {
      val credential = PhoneAuthProvider.getCredential(verificationId, code)
      val result = auth.signInWithCredential(credential).await()
      
      // Verificar si es un usuario nuevo
      val isNewUser = result.additionalUserInfo?.isNewUser ?: false
      
      // Si es un usuario nuevo, crear el documento en Firestore
      if (isNewUser) {
        val uid = result.user?.uid ?: throw Exception("Error creando usuario con teléfono")
        val userData = hashMapOf(
          "username" to "Usuario", // Nombre genérico
          "lukitas" to 30, // Monto inicial
          "phone" to result.user?.phoneNumber // Guardar el número de teléfono
        )
        
        firestore.collection("usuarios").document(uid).set(userData).await()
      }
      
      true
    } catch (e: Exception) {
      false
    }
  }
  
  // Método auxiliar para iniciar sesión con credencial de teléfono
  private fun signInWithPhoneCredential(
    credential: PhoneAuthCredential,
    callback: (Boolean, Exception?) -> Unit
  ) {
    auth.signInWithCredential(credential)
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          // Inicio de sesión exitoso
          val user = task.result?.user
          callback(true, null)
        } else {
          // Error en el inicio de sesión
          callback(false, task.exception)
        }
      }
  }

  /* Este método NO puede utilizarse según TOS de Facebook
  override suspend fun signInWithFacebookProvider(activity: ComponentActivity): Boolean {
    return try {
      val provider = com.google.firebase.auth.OAuthProvider.newBuilder("facebook.com").build()
      val result = auth.startActivityForSignInWithProvider(activity, provider).await()
      
      // Verificar si es un usuario nuevo
      val isNewUser = result.additionalUserInfo?.isNewUser ?: false
      
      // Si es un usuario nuevo, crear el documento en Firestore
      if (isNewUser) {
        val uid = result.user?.uid ?: throw Exception("Error creando usuario con Facebook")
        val userData = hashMapOf(
          "username" to (result.user?.displayName ?: "Usuario"),
          "lukitas" to 30, // Monto inicial
          "email" to result.user?.email
        )
        
        firestore.collection("usuarios").document(uid).set(userData).await()
      }
      
      true
    } catch (e: Exception) {
      android.util.Log.e("FacebookAuth", "Error en autenticación con Facebook Provider", e)
      false
    }
  }
  */
}