package com.puyodev.luka

import android.app.Application
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LukaHiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Es importante configurar todo antes de inicializar el SDK
            FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
            FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
            FacebookSdk.setAutoLogAppEventsEnabled(true)
            
            // Usar la API más reciente (v22.0)
            FacebookSdk.setGraphApiVersion("v22.0")
            
            // Deshabilitar optimizaciones que pueden causar problemas
            FacebookSdk.setAutoInitEnabled(true)
            FacebookSdk.setAdvertiserIDCollectionEnabled(true)
            
            // Inicializa el SDK
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(this)
            
            // Habilitar logging para depuración
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
            
            Log.d("FacebookSDK", "Facebook SDK inicializado correctamente con API v22.0")
            Log.d("FacebookSDK", "App ID: ${FacebookSdk.getApplicationId()}")
            Log.d("FacebookSDK", "Client Token: ${FacebookSdk.getClientToken()}")
        } catch (e: Exception) {
            Log.e("FacebookSDK", "Error inicializando Facebook SDK: ${e.message}", e)
        }
    }
}
