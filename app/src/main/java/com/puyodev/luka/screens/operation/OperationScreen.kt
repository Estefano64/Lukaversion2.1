package com.puyodev.luka.screens.operation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.puyodev.luka.OPERATION_DETAILS_SCREEN
import com.puyodev.luka.common.composable.ActionToolbar
import com.puyodev.luka.common.ext.toolbarActions
import com.puyodev.luka.model.Operation
import com.puyodev.luka.model.User
import com.puyodev.luka.screens.drawer.DrawerHeader
import com.puyodev.luka.screens.drawer.DrawerScreen
import com.puyodev.luka.screens.pay.openGoogleMapsWithCurrentLocation
import kotlinx.coroutines.launch

@Composable
fun OperationsScreen(
    openScreen: (String) -> Unit,
    viewModel: OperationsViewModel = hiltViewModel()
) {
    // Observa un único objeto User en lugar de una lista
    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = User())

    val operations = viewModel
        .operations
        .collectAsStateWithLifecycle(emptyList())

    OperationsScreenContent(
        user = user,
        operations = operations,
        onProfileClick = viewModel::onProfileClick,
        openScreen = openScreen,
    )

    //LaunchedEffect(viewModel) { viewModel.loadOperationOptions() }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OperationsScreenContent(
    user: User,
    onProfileClick: ((String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    operations: State<List<Operation>>,
    openScreen: (String) -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed) // Estado para abrir/cerrar el drawer
    val scope = rememberCoroutineScope() // Alcance de la corrutina para manejar el drawer
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    ModalNavigationDrawer(
        drawerState = drawerState, // Controla si el drawer está abierto o cerrado
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                DrawerHeader(user = user.username)
                Spacer(modifier = Modifier.height(16.dp)) // Espacio desde el borde superior
                DrawerScreen(openScreen = openScreen) // Pasando el parámetro requerido
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                ActionToolbar(
                    title = user.username,  // Muestra el nombre de usuario
                    modifier = Modifier.toolbarActions(),
                    //endActionIcon = AppIcon.ic_settings,
                    startAction = { openGoogleMapsWithCurrentLocation(fusedLocationClient, context) },
                    endAction = { onProfileClick(openScreen) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Fondo de la barra
                    contentColor = MaterialTheme.colorScheme.inversePrimary, // Color de texto e iconos
                    onMenuClick = {
                        scope.launch { drawerState.open() } // Abre el drawer al hacer clic en el menú
                    }
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(operations.value, key = { it.id }) { operationItem ->
                    OperationItem(
                        operation = operationItem,
                        onClick = { selectedOperation ->
                            openScreen("$OPERATION_DETAILS_SCREEN/${selectedOperation.id}")
                        }
                    )
                }
            }
        }
    }
}


