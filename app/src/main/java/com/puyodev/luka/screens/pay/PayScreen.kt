package com.puyodev.luka.screens.pay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.puyodev.luka.R
import com.puyodev.luka.common.composable.ActionToolbar
import com.puyodev.luka.common.ext.toolbarActions
import com.puyodev.luka.model.User
import com.puyodev.luka.screens.drawer.DrawerHeader
import com.puyodev.luka.screens.drawer.DrawerScreen
import kotlinx.coroutines.delay
//import com.example.makeitso.model.Task
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KFunction3

fun openGoogleMapsWithCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val geoUri = "geo:$latitude,$longitude?q=$latitude,$longitude"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            } else {
                Log.e("Location", "Ubicación no disponible")
            }
        }.addOnFailureListener { exception ->
            Log.e("Location", "Error obteniendo la ubicación: ${exception.message}")
        }
    } catch (e: SecurityException) {
        Log.e("Location", "No se tienen los permisos necesarios para obtener la ubicación")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PayScreen(
    openScreen: (String) -> Unit,
    viewModel: PayViewModel = hiltViewModel(),
) {
    // Observa un único objeto User en lugar de una lista
    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = User())
    val nfcStatus by viewModel.nfcStatus.collectAsState() // Añadido el estado NFC
    val locationText = remember { mutableStateOf("Ubicación no disponible") }

    PayScreenContent(
        user = user,
        onProfileClick = viewModel::onProfileClick,
        onTicketClick = viewModel::onTicketClick,
        onProfilePaymentGatewayClick = viewModel::onProfilePaymentGatewayClick,
        openScreen = openScreen,
        nfcStatus = nfcStatus,
        locationText = locationText

    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun PayScreenContent(
    user: User,
    onProfileClick: ((String) -> Unit) -> Unit,
    onProfilePaymentGatewayClick: ((String) -> Unit) -> Unit,
    onTicketClick: KFunction3<(String) -> Unit, Int, String, Unit>,
    openScreen: (String) -> Unit,
    nfcStatus: PayViewModel.NFCStatus,
    locationText: MutableState<String>
) {
    var valor by remember { mutableIntStateOf(1) } // Estado del contador
    val drawerState = rememberDrawerState(DrawerValue.Closed) // Estado para abrir/cerrar el drawer
    val scope = rememberCoroutineScope() // Alcance de la corrutina para manejar el drawer
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGoogleMapsWithCurrentLocation(fusedLocationClient, context)
        }
    }

    fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Estado para animar la escala
    var animateScaleTrigger by remember { mutableStateOf(false) } // Disparador de escala
    val scale by animateFloatAsState(
        targetValue = if (animateScaleTrigger) 1.2f else 1f, // Escala animada
        animationSpec = tween(durationMillis = 300), // Duración de la animación
        finishedListener = { animateScaleTrigger = false } // Reinicia el disparador
    )

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
                        title = user.username, // Muestra el nombre de usuario
                        modifier = Modifier.toolbarActions(),
                        containerColor = MaterialTheme.colorScheme.primaryContainer, // Fondo de la barra
                        contentColor = MaterialTheme.colorScheme.inversePrimary, // Color de texto e iconos
                        startAction = { openGoogleMapsWithCurrentLocation(fusedLocationClient, context) },
                        endAction = { onProfileClick(openScreen) },
                        onMenuClick = {
                            scope.launch { drawerState.open() } // Abre el drawer al hacer clic en el menú
                        }
                    )
                },
                bottomBar = { CustomBottomBar(locationText = locationText) },
                ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de forma uniforme
                    ) {
                        OutlinedButton(
                            modifier = Modifier.width(160.dp),
                            onClick = { onProfilePaymentGatewayClick(openScreen)}
                        ) {
                            Column {
                                Text(
                                    text = "Lukitas: ",
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre el ícono y el texto
                                ) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.lukita_coin),
                                        contentDescription = "Lukitas Icono",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Unspecified

                                    )
                                    Text(
                                        text = "${user.lukitas}",
                                    )
                                }
                            }
                        }
                        OutlinedButton(
                            modifier = Modifier.width(160.dp),
                            onClick = { /*TODO*/ }
                        ) {
                            Column {
                                Text(
                                    text = "Tarifa: ",
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaciado entre el ícono y el texto
                                ) {

                                    Icon(
                                        painter = painterResource(id = R.drawable.lukita_coin),
                                        contentDescription = "Lukitas Icono",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Unspecified

                                    )
                                    Text(
                                        text = "1",
                                        //text = "${user.lukitas}",
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically // Centra verticalmente el contenido del Row
                        ) {
                            IconButton(
                                onClick = {
                                    if (valor > 1) {
                                        valor--
                                        animateScaleTrigger = true // Dispara la animación de escala
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Decrementar")
                            }
                            Text(
                                text = "$valor",
                                fontSize = 100.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.scale(scale) // Aplica escala animada
                            )

                            IconButton(
                                onClick = {
                                    if (valor < 10) {
                                        valor++
                                        animateScaleTrigger = true // Dispara la animación de escala
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Incrementar")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // LazyVerticalGrid con GridCells.Adaptive para ajustar el tamaño de las imágenes
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 60.dp), // El tamaño mínimo que se ajusta según el espacio
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(valor) { index ->
                            AnimatedVisibility(
                                visible = index < valor,
                                enter = fadeIn(animationSpec = tween(500)), // Animación de entrada
                                exit = fadeOut(animationSpec = tween(500)) // Animación de salida
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.person),
                                    contentDescription = null,
                                    colorFilter = if (isDarkTheme) ColorFilter.tint(Color.LightGray) else null,

                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Gray, CircleShape)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (nfcStatus) {
                            is PayViewModel.NFCStatus.Idle -> {
                                ExtendedFloatingActionButton(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(0.dp, 40.dp),
                                    onClick = {
                                        onTicketClick(
                                            openScreen,
                                            valor,
                                            "Urb. Monterrey D-8, José Luis Bustamante y Rivero"
                                        )
                                    },
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.inversePrimary,
                                    icon = {
                                        Icon(
                                            Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Realizar pago"
                                        )
                                    },
                                    text = { Text(text = "Pagar", fontSize = 20.sp) }
                                )
                            }
                            is PayViewModel.NFCStatus.WaitingForNFC -> {
                                NFCWaitingIndicator()
                            }
                            is PayViewModel.NFCStatus.Success -> {
                                // No mostrar nada, la navegación ocurrirá automáticamente
                            }
                            is PayViewModel.NFCStatus.Error -> {
                                ErrorMessage(message = nfcStatus.message)
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun NFCWaitingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Acerque su teléfono al lector...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CustomBottomBar(locationText: MutableState<String>) {
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val geocoder = Geocoder(context, Locale("es"))

    // Función para obtener la ubicación actual
    fun updateLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        locationText.value = address.getAddressLine(0)
                    } else {
                        locationText.value = "Ubicación no disponible"
                    }
                } else {
                    locationText.value = "Ubicación no disponible"
                }
            }.addOnFailureListener { exception ->
                locationText.value = "Error obteniendo la ubicación: ${exception.message}"
            }
        } catch (e: SecurityException) {
            locationText.value = "No se tienen los permisos necesarios para obtener la ubicación"
        }
    }
    BottomAppBar(
        modifier = Modifier
            .shadow(elevation = 10.dp)
            .height(150.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Asegura que las columnas queden separadas
        ) {
            Column(
                modifier = Modifier
                    .weight(1f) // Cada columna ocupa el mismo ancho
                    .padding(end = 8.dp) // Espacio opcional entre las columnas
            ) {
                Text(text = "Ubicación Actual:", fontSize = 15.sp)
                Text(
                    text = "Urb. Monterrey D-8, José Luis Bustamante y Rivero",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.45f) // Cada columna ocupa el mismo ancho
                    .padding(start = 8.dp) // Espacio opcional entre las columnas
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(), // Hace que el Box ocupe todo el espacio de la columna
                    contentAlignment = Alignment.Center // Centra la imagen dentro del Box
                ) {
                    IconButton(onClick = { updateLocation() }) {

                    Image(
                        painter = painterResource(id = R.drawable.located),
                        contentDescription = null,
                        colorFilter = if (isDarkTheme) ColorFilter.tint(Color.LightGray) else null,
                        modifier = Modifier
                            .size(80.dp) // Tamaño personalizado para la imagen centrada
                    )}
                }
            }
        }
    }
}