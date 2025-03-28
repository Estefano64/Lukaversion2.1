package com.puyodev.luka.screens.profile

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.puyodev.luka.SPLASH_SCREEN
import com.puyodev.luka.model.service.AccountService
import com.puyodev.luka.model.service.LogService
import com.puyodev.luka.model.service.StorageService
//import com.puyodev.luka.model.service.StorageService
import com.puyodev.luka.screens.LukaViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
    private val storageService: StorageService
) : LukaViewModel(logService) {

    // Usuario autenticado en Firebase Authentication
    private val firebaseUser = Firebase.auth.currentUser
    val user = storageService.currentUserData
    val userEmail = firebaseUser?.email ?: ""

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(SPLASH_SCREEN)
        }
    }

    fun onDeleteMyAccountClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(SPLASH_SCREEN)
        }
    }

}