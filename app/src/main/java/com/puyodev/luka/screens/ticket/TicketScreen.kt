
package com.puyodev.luka.screens.ticket

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.puyodev.luka.R
import com.puyodev.luka.model.User
import com.puyodev.luka.screens.pay.openGoogleMapsWithCurrentLocation
//import com.example.makeitso.model.Task
import com.puyodev.luka.ui.theme.LukaTheme
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun TicketScreen(
    valor: Int?,
    direccion: String?,
    openScreen: (String) -> Unit,
    viewModel: TicketViewModel = hiltViewModel(),
) {
    // Observa un único objeto User en lugar de una lista
    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = User())
    val context = LocalContext.current
    val view = LocalView.current

    var showConfetti by remember { mutableStateOf(false) }

    // Inicia la animación cuando se carga la pantalla
    LaunchedEffect(Unit) {
        showConfetti = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mostrar confetti cuando showConfetti sea verdadero
        if (showConfetti) {
            KonfettiView(
                modifier = Modifier.fillMaxSize().zIndex(1f),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(duration = 3, timeUnit = TimeUnit.SECONDS).perSecond(50),
                        position = Position.Relative(0.5, 0.20),
                    )
                )
            )
        }

        // Tu pantalla de contenido
        TicketScreenContent(
            user = user,
            valor = valor,
            direccion = direccion,
            onPayScreenClick = viewModel::onPayScreenClick,
            onShareClick = {
                val uri = viewModel.captureAndShareImage(view, context)
                if (uri != null) {
                    viewModel.shareImage(context, uri)
                }
            },
            openScreen = openScreen
        )
    }
}

@Composable
fun TicketScreenContent(
    modifier: Modifier = Modifier,
    user: User, // Añade este parámetro
    valor: Int?,  // Recibe el parámetro valor
    direccion: String?,  // Recibe el parámetro direccion
    onPayScreenClick: ((String) -> Unit) -> Unit,
    onShareClick: () -> Unit,
    openScreen: (String) -> Unit

) {

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val fecha = "02/12/2024"

    Column(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    // use Brush.verticalGradeint to change the angle
                    colors = listOf(
                        Color(0xFF64B5F6), // Start color
                        Color(0xFF0D47A1)  // End color
                    )
                )
            )
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 80.dp, 0.dp, 0.dp),
            horizontalArrangement = Arrangement.Center // Centra los elementos dentro de Row
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_luka),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp), // Tamaño personalizado para la imagen centrada
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Centra el Box en la pantalla
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center // Centra el Box en la pantalla
                ) {
                    Text(text = fecha,
                        color = Color.DarkGray
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(0.dp, 40.dp)
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.Center, // Centra los elementos verticalmente
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bus),
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp) // Tamaño personalizado para la imagen centrada
                        )
                        Text(text = "Bus 101",
                            color = Color.DarkGray
                        )
                    }
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.Center, // Centra los elementos verticalmente
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center // Centra el contenido del Row horizontalmente

                        ) {
                            Text(
                                text = "Pago:\nS/$valor",
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray

                            )
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center // Centra el contenido del Row horizontalmente

                        ) {
                            Text(
                                text = "Paradero:\n$direccion",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de forma uniforme
        ) {
            SmallFloatingActionButton(
                onClick = onShareClick,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.small // Aplica sombra en la forma definida
                )
            ) {
                Icon(
                    Icons.Outlined.Share,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp),
                    contentDescription = "Compartir voucher Luka" // Add a valid content description
                )
            }
            SmallFloatingActionButton(
                onClick = { onPayScreenClick(openScreen) },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.shadow(
                        elevation = 8.dp,
                shape = MaterialTheme.shapes.small // Aplica sombra en la forma definida
            )
            ) {
                Icon(
                    Icons.Filled.Menu,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp),
                    contentDescription = "Regresar a la pantalla principal" // Add a valid content description
                )
            }
            SmallFloatingActionButton(
                onClick = { openGoogleMapsWithCurrentLocation(fusedLocationClient, context) },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.small // Aplica sombra en la forma definida
                )
            ) {
                Icon(
                    Icons.Outlined.Place,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp),
                    contentDescription = "" // Add a valid content description
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun TicketScreenPreview() {
    LukaTheme {
        TicketScreen(
            valor = 5,  // Ejemplo de valor para "Pago"
            direccion = "Av. Principal",  // Ejemplo de dirección o paradero
            openScreen = {}  // Acción de navegación vacía para el Preview
        )
    }
}