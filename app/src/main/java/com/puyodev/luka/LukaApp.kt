package com.puyodev.luka

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.puyodev.luka.common.snackbar.SnackbarManager
import com.puyodev.luka.screens.PaymentGateway.PaymentGatewayScreen
import com.puyodev.luka.screens.info.InfoScreen
//import com.puyodev.luka.screens.AppContent
import com.puyodev.luka.screens.pay.PayScreen
import com.puyodev.luka.screens.profile.ProfileScreen
//import com.example.makeitso.screens.edit_task.EditTaskScreen
import com.puyodev.luka.screens.login.LoginScreen
import com.puyodev.luka.screens.operation.OperationsScreen
import com.puyodev.luka.screens.operation_details.OperationDetailsScreen
//import com.puyodev.luka.screens.settings.SettingsScreen
import com.puyodev.luka.screens.sign_up.SignUpScreen
import com.puyodev.luka.screens.splash.SplashScreen
import com.puyodev.luka.screens.ticket.TicketScreen
//import com.example.makeitso.screens.tasks.TasksScreen
import com.puyodev.luka.ui.theme.LukaTheme
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.google.accompanist.permissions.rememberPermissionState
//import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.MutableState
import com.puyodev.luka.screens.phone.PhoneAuthScreen


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LukaApp(locationText: MutableState<String>) {
    LukaTheme {
        /*Para solicitar permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestNotificationPermissionDialog()
        }
        */
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            //estructura de la pantalla
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = appState.snackbarState,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                        }
                    )
                },
            ) {
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.fillMaxSize()
                ) {
                    lukaGraph(appState)
                }
            }
        }
    }
}

/* funcion para obtener el permiso muestar un dialog al user
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog() {
    val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) RationaleDialog()
        else PermissionDialog { permissionState.launchPermissionRequest() }
    }
}
*/

//estado de la aplicacion
@Composable
fun rememberAppState(
    snackbarState: SnackbarHostState = remember { SnackbarHostState() }, // Cambia a SnackbarHostState
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    //recordaremos estos parametros y almacenado en LukaAppState clase que facilita asi su acceso a lo largo de la aplicación
    remember(snackbarState, navController, snackbarManager, resources, coroutineScope) {
        LukaAppState(snackbarState, navController, snackbarManager, resources, coroutineScope)
    }

@Composable
@ReadOnlyComposable
//Permite acceso a los recursos actuales del contexto
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

//se define el gráfico de navegación - se agrupa todas las rutas y screens respectivas
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.lukaGraph(appState: LukaAppState) {


    /*
    composable(SETTINGS_SCREEN) {
        SettingsScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }
*/
    composable(LOGIN_SCREEN) {
        LoginScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(PHONE_AUTH_SCREEN) {
        PhoneAuthScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(
        route = "$TICKET_SCREEN/{valor}/{direccion}",
        arguments = listOf(
            navArgument("valor") { type = NavType.IntType },
            navArgument("direccion") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        // Obtén los argumentos pasados
        val valor = backStackEntry.arguments?.getInt("valor")
        val direccion = backStackEntry.arguments?.getString("direccion")

        // Pasa los argumentos a la TicketScreen
        TicketScreen(
            valor = valor,
            direccion = direccion,
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SIGNUP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(PAY_SCREEN){
        PayScreen(openScreen = { route -> appState.navigate(route) })
    }

    composable(INFO_SCREEN){
        InfoScreen(openScreen = { route -> appState.navigate(route) })
    }

    composable(PAYMENT_SCREEN){
        PaymentGatewayScreen(openScreen = { route -> appState.navigate(route) })
    }

    composable(PROFILE_SCREEN){
        ProfileScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(OPERATIONS_SCREEN) { OperationsScreen(openScreen = { route -> appState.navigate(route) }) }

    composable(
        route = "$OPERATION_DETAILS_SCREEN/{$OPERATION_ID}",
        arguments = listOf(navArgument(OPERATION_ID) {
            nullable = true // Permite que el argumento sea opcional
            defaultValue = null // Valor predeterminado cuando no se pasa
        })
    ) { backStackEntry ->
        val operationId = backStackEntry.arguments?.getString(OPERATION_ID)
        OperationDetailsScreen(
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

