package com.puyodev.luka.screens.info

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.puyodev.luka.R
import com.puyodev.luka.screens.drawer.DrawerHeader
import com.puyodev.luka.screens.drawer.DrawerScreen
import com.puyodev.luka.common.composable.ActionToolbar
import com.puyodev.luka.common.ext.toolbarActions
import com.puyodev.luka.model.User
import com.puyodev.luka.screens.pay.openGoogleMapsWithCurrentLocation
import kotlinx.coroutines.launch

@Composable
fun InfoScreen(
    openScreen: (String) -> Unit,
    viewModel: InfoViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = User())

    InfoScreenContent(
        user = user,
        onProfileClick = viewModel::onProfileClick,
        openScreen = openScreen,
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InfoScreenContent(
    user: User,
    onProfileClick: ((String) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    openScreen: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                DrawerHeader(user = user.username)
                Spacer(modifier = Modifier.height(16.dp))
                DrawerScreen(openScreen = openScreen)
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                ActionToolbar(
                    title = "Acerca de Luka",
                    modifier = Modifier.toolbarActions(),
                    startAction = { openGoogleMapsWithCurrentLocation(fusedLocationClient, context) },
                    endAction = { onProfileClick(openScreen) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Fondo de la barra
                    contentColor = MaterialTheme.colorScheme.inversePrimary, // Color de texto e iconos
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF0087D9)) // Fondo con el color de Luka
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Logo centrado
                Image(
                    painter = painterResource(id = R.drawable.logo_luka), // Cambiar por tu logo
                    contentDescription = "Logo de Luka",
                    modifier = Modifier
                        .size(150.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )

                Text(
                    text = "Luka es una aplicación diseñada para facilitar pagos rápidos del transporte público mediante tecnología NFC.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp,0.dp,16.dp,16.dp)
                )

                // Agregar múltiples tarjetas con contenido adicional
                InfoCard(
                    title = "Términos y Condiciones",
                    description = "Consulta los términos y condiciones de uso de Luka.",
                    onClick = { /* Acción para ver Términos y Condiciones */ }
                )
                InfoCard(
                    title = "Política de Privacidad",
                    description = "Revisa cómo protegemos tus datos personales.",
                    onClick = { /* Acción para ver Política de Privacidad */ }
                )
                InfoCard(
                    title = "Aprende más sobre Luka",
                    description = "Descubre todas las funcionalidades que tenemos.",
                    onClick = { /* Acción para ver más información */ }
                )
                InfoCard(
                    title = "Colaboraciones de Luka",
                    description = "Explora nuestras alianzas y colaboraciones.",
                    onClick = { /* Acción para explorar colaboraciones */ }
                )
                InfoCard(
                    title = "Soporte Técnico",
                    description = "Obtén ayuda con cualquier problema o consulta.",
                    onClick = { /* Acción para abrir soporte técnico */ }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun InfoCard(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Cambiar ícono si es necesario
                contentDescription = "Icono de Información",
                tint = Color.Gray
            )
        }
    }
}
