package com.puyodev.luka.screens.operation_details

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.puyodev.luka.R
import com.puyodev.luka.common.ext.hasCreatedDateTime
import com.puyodev.luka.model.Operation
import com.puyodev.luka.screens.pay.openGoogleMapsWithCurrentLocation
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OperationDetailsScreen(
    openScreen: (String) -> Unit,
    viewModel: OperationDetailsViewModel = hiltViewModel()
) {
    val operation by viewModel.operation
    val context = LocalContext.current
    val view = LocalView.current

    OperationDetailsScreenContent(
        operation = operation,
        onPayScreenClick = viewModel::onPayScreenClick,
        onShareClick = {
            val uri = viewModel.captureAndShareImage(view, context)
            if (uri != null) {
                viewModel.shareImage(context, uri)
            }
        },
        openScreen = openScreen,
    )
}

@Composable
fun OperationDetailsScreenContent(
    modifier: Modifier = Modifier,
    operation: Operation,
    onPayScreenClick: ((String) -> Unit) -> Unit,
    onShareClick: () -> Unit,
    openScreen: (String) -> Unit
) {

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
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
                    Text(text = formatTimestamp(operation.completedTimestamp), color = Color.DarkGray)

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
                        Text(text = "Bus 101", color = Color.DarkGray)
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
                                text = "Pago:\nS/${operation.mount}",
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
                                text = "Paradero:\n${operation.busStop}",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp, color = Color.DarkGray
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
                    contentDescription = "Obtener ubicacion actual"
                )
            }
        }
    }
}

@Composable
fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "N/A"

    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(timestamp.toDate())
}