package com.puyodev.luka

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.puyodev.luka.ui.theme.LukaTheme
import dagger.hilt.android.AndroidEntryPoint

import android.app.AlertDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.puyodev.luka.screens.PaymentGateway.PayPalConfig
import com.puyodev.luka.screens.pay.PayViewModel
import javax.inject.Inject

// Agrega esta anotación
@AndroidEntryPoint
class MainActivity : ComponentActivity()/*, NfcAdapter.ReaderCallback*/ {

    //private var nfcAdapter: NfcAdapter? = null
    // Inject PayPalConfig using Hilt
    @Inject
    lateinit var payPalConfig: PayPalConfig

    // Create a variable to handle payment lifecycle
    private val paymentLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            // Initialize PayPal when the activity is created
            payPalConfig.initialize()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            // Clean up PayPal resources when the activity is destroyed
            payPalConfig.cleanup()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(paymentLifecycleObserver)

        enableEdgeToEdge()

        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current
            val locationText = remember { mutableStateOf("Ubicación no disponible") }

            DisposableEffect(lifecycleOwner) {
                onDispose {
                    // Clean up when the composition is disposed
                    payPalConfig.cleanup()
                }
            }

            LukaApp(locationText = locationText)
        }
    }
    
    // Añadir manejo para resultados de actividad
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Permitir que el CallbackManager de Facebook maneje el resultado de la autenticación
        val callbackManager = com.facebook.CallbackManager.Factory.create()
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return
        }
        
        Log.d("MainActivity", "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
    }

    override fun onDestroy() {
        // Remove the lifecycle observer before destroying the activity
        lifecycle.removeObserver(paymentLifecycleObserver)
        super.onDestroy()
    }

    @Composable
    fun MyApp(content: @Composable () -> Unit) {
        LukaTheme {
            Surface {
                content()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MyApp {
            Text("Hello NFC!")
        }
    }
}