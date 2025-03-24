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

            FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
            FacebookSdk.setClientToken(getString(R.string.facebook_client_token))
            FacebookSdk.setAutoLogAppEventsEnabled(true)
            FacebookSdk.setAutoInitEnabled(true)
            FacebookSdk.fullyInitialize()
            
            // Inicializa el SDK
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(this)
            
            // Habilitar logging para depuración
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO)
            
            Log.d("FacebookSDK", "Facebook SDK inicializado correctamente")
        } catch (e: Exception) {
            Log.e("FacebookSDK", "Error inicializando Facebook SDK: ${e.message}", e)
        }
    }
}
