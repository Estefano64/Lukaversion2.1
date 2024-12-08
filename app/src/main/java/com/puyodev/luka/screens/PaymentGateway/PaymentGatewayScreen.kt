package com.puyodev.luka.screens.PaymentGateway
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.puyodev.luka.R
import com.puyodev.luka.screens.drawer.DrawerHeader
import com.puyodev.luka.screens.drawer.DrawerScreen
import com.puyodev.luka.common.composable.ActionToolbar
import com.puyodev.luka.common.ext.toolbarActions
import com.puyodev.luka.model.User
import com.puyodev.luka.screens.PaymentGateway.PaymentGatewayViewModel.Companion.LUKITA_TO_USD_RATE
import com.puyodev.luka.screens.operation.OperationsViewModel
import com.puyodev.luka.screens.pay.openGoogleMapsWithCurrentLocation
import kotlinx.coroutines.launch


@Composable
fun PaymentGatewayScreen(
    openScreen: (String) -> Unit,
    viewModel: PaymentGatewayViewModel = hiltViewModel()
) {
    // Observa un único objeto User en lugar de una lista
    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = User())
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    //val balance by viewModel.balance.collectAsStateWithLifecycle(initialValue = 0.0)

    // Efecto para manejar los cambios de estado de pago
    LaunchedEffect(paymentStatus) {
        when (paymentStatus) {
            is PaymentGatewayState.PaymentStatus.Loading -> {
                showLoadingDialog = true
                showSuccessDialog = false
                showErrorDialog = false
            }
            is PaymentGatewayState.PaymentStatus.Processing -> {
                showLoadingDialog = true  // We typically want to show loading during processing too
                showSuccessDialog = false
                showErrorDialog = false
            }
            is PaymentGatewayState.PaymentStatus.Verifying -> {
                showLoadingDialog = true  // Keep loading visible during verification
                showSuccessDialog = false
                showErrorDialog = false
            }
            is PaymentGatewayState.PaymentStatus.Success -> {
                showLoadingDialog = false
                showSuccessDialog = true
                showErrorDialog = false
            }
            is PaymentGatewayState.PaymentStatus.Error -> {
                showLoadingDialog = false
                showSuccessDialog = false
                showErrorDialog = true
//                errorMessage = paymentStatus.message
            }
            PaymentGatewayState.PaymentStatus.Cancelled -> {
                showLoadingDialog = false
                showSuccessDialog = false
                showErrorDialog = false
                // Optionally show a toast or snackbar indicating cancellation
            }
            PaymentGatewayState.PaymentStatus.Idle -> {
                showLoadingDialog = false
                showSuccessDialog = false
                showErrorDialog = false
            }
        }
    }
    // Handle dialogs here in the main screen
    if (showLoadingDialog) {
        LoadingDialog(
            onDismiss = {
                showLoadingDialog = false
                viewModel.cancelPayment() // Add this function to your ViewModel
            }
        )
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                viewModel.resetPaymentStatus() // Add this function to your ViewModel
            }
        )
    }

    if (showErrorDialog) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
                viewModel.resetPaymentStatus() // Add this function to your ViewModel
            }
        )
    }


    PaymentGatewayScreenContent(
        user = user,
        onProfileClick = viewModel::onProfileClick,
        openScreen = openScreen,
        viewModel = viewModel,
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentGatewayScreenContent(
    user: User,
    viewModel: PaymentGatewayViewModel,
    onProfileClick: ((String) -> Unit) -> Unit,
    openScreen: (String) -> Unit,

) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedAmount by remember { mutableStateOf("0") }
    var rechargeAmount by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val paymentStatus by viewModel.paymentStatus.collectAsState()
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
                    title = "Recargar Saldo",
                    modifier = Modifier.toolbarActions(),
                    startAction = { openGoogleMapsWithCurrentLocation(fusedLocationClient, context) },
                    endAction = { onProfileClick(openScreen)},
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Sección de Lukitas disponibles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Disponibles: ",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.lukita_coin),
                        contentDescription = "Lukitas Icono",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "${user.lukitas}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                // Campo para ingresar el monto de recarga
                OutlinedTextField(
                    value = rechargeAmount,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 3) {
                            rechargeAmount = it
                            selectedAmount = it.ifEmpty { "0" }
                        }
                    },
                    label = { Text("Monto de Recarga") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var textFieldFocusRequester = remember { FocusRequester() }

                    if (isEditing) {
                        BasicTextField(
                            value = selectedAmount,
                            onValueChange = { newValue ->
                                if (newValue.length <= 3 && newValue.all { it.isDigit() }) {
                                    selectedAmount = newValue
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    isEditing = false
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            ),
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(textFieldFocusRequester)
                                .onFocusChanged {
                                    if (!it.isFocused) {
                                        isEditing = false
                                    }
                                }
                        )

                        LaunchedEffect(Unit) {
                            textFieldFocusRequester.requestFocus()
                            keyboardController?.show()
                        }
                    } else {
                        Text(
                            text = "$selectedAmount Lukas",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .clickable {
                                    isEditing = true
                                }
                        )
                    }
                }

                // Botones de montos predefinidos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PresetAmountButton(
                        amount = 6,
                        selectedAmount = selectedAmount.toIntOrNull() ?: 0
                    ) {
                        selectedAmount = "6"
                        isEditing = false
                    }
                    PresetAmountButton(
                        amount = 10,
                        selectedAmount = selectedAmount.toIntOrNull() ?: 0
                    ) {
                        selectedAmount = "10"
                        isEditing = false
                    }
                    PresetAmountButton(
                        amount = 12,
                        selectedAmount = selectedAmount.toIntOrNull() ?: 0
                    ) {
                        selectedAmount = "12"
                        isEditing = false
                    }
                }

                //Configuración PayPal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModel.processPayment(selectedAmount.toIntOrNull() ?: 0)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0070BA)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Recargar")
                        }
                    }
                }

                // Diálogo de carga
                if (paymentStatus is PaymentGatewayState.PaymentStatus.Loading) {
                    LoadingDialog(onDismiss = {})
                }

                // Imagen promocional
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Promociones",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.anuncio),
                        contentDescription = "Promoción de reciclaje",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetAmountButton(
    amount: Int,
    selectedAmount: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            if (selectedAmount == amount) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selectedAmount == amount)
                MaterialTheme.colorScheme.inversePrimary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$amount Lukas")
        }
    }
}
@Composable
fun LoadingDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,  // Allow dismissal
        properties = DialogProperties(
            dismissOnBackPress = true,  // Allow back button to dismiss
            dismissOnClickOutside = false  // Prevent accidental dismissal
        )
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Procesando pago...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "¡Pago Exitoso!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tu saldo ha sido actualizado correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Error en el Pago",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPaymentGatewayScreen() {
    PaymentGatewayScreen(openScreen = {})

}