package com.puyodev.luka.screens.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puyodev.luka.R
import com.puyodev.luka.screens.NavigationItems
import kotlinx.coroutines.launch

@Composable
fun DrawerScreen(
    openScreen: (String) -> Unit,
    viewModel: DrawerViewModel = hiltViewModel(),
) {
    DrawerContent(
        onPayScreenClick = viewModel::onPayScreenClick,
        onInfoScreenClick = viewModel::onInfoScreenClick,
        onRechargeScreenClick = viewModel::onRechargeScreenClick,
        //onNotificationScreenClick = viewModel::onNotificationScreenClick,
        onConfigurationScreenClick = viewModel::onConfigurationScreenClick,
        onHistoryScreenClick = viewModel::onHistoryScreenClick,
        openScreen = openScreen
    )
}

@Composable
fun DrawerHeader(user: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {

        Image(
            painterResource(id = R.drawable.neko),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(5.dp))

        Text(
            text = user,
            textAlign = TextAlign.Center,
            //style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.inversePrimary,
        )
    }
}

@Composable
fun DrawerContent(
    onPayScreenClick: ((String) -> Unit) -> Unit,
    onInfoScreenClick: ((String) -> Unit) -> Unit,
    onRechargeScreenClick: ((String) -> Unit) -> Unit,
    //onNotificationScreenClick: ((String) -> Unit) -> Unit,
    onConfigurationScreenClick: ((String) -> Unit) -> Unit,
    onHistoryScreenClick: ((String) -> Unit) -> Unit,
    openScreen: (String) -> Unit
) {
    val items = listOf(
        NavigationItems(
            title = "Inicio",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItems(
            title = "Info",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info
        ),
        NavigationItems(
            title = "Recargar",
            selectedIcon = Icons.Filled.Refresh,
            unselectedIcon = Icons.Outlined.Refresh
        ),
        /*NavigationItems(
            title = "Notificaciones",
            selectedIcon = Icons.Filled.Notifications,
            unselectedIcon = Icons.Outlined.Notifications,
            badgeCount = 10
        ),*/
        NavigationItems(
            title = "Configuración",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),
        NavigationItems(
            title = "Historial",
            selectedIcon = Icons.Filled.Email,
            unselectedIcon = Icons.Outlined.Email
        )
    )

    //Remember Clicked index state
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    items.forEachIndexed { index, item ->
        Spacer(modifier = Modifier.height(16.dp)) //space (margin) from top
        NavigationDrawerItem(
            label = { Text(text = item.title) },
            selected = index == selectedItemIndex,
            onClick = {
                selectedItemIndex = index
                // Usa el appState para la navegación según el título seleccionado
                when (item.title) {
                    "Inicio" -> { onPayScreenClick(openScreen)}
                    "Info" -> { onInfoScreenClick(openScreen)}
                    //"Notificaciones" -> { onNotificationScreenClick(openScreen)}
                    "Recargar" -> { onRechargeScreenClick(openScreen)}
                    "Configuración" -> { onConfigurationScreenClick(openScreen)}
                    "Historial" -> { onHistoryScreenClick(openScreen)}
                }
                scope.launch {
                    drawerState.close()
                }
            },
            icon = {
                Icon(
                    imageVector = if (index == selectedItemIndex) {
                        item.selectedIcon
                    } else item.unselectedIcon,
                    contentDescription = item.title
                )
            },
            shape = MaterialTheme.shapes.small,
            badge = {  // Show Badge
                item.badgeCount?.let {
                    Text(text = item.badgeCount.toString())
                }
            },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding) //padding between items
        )
    }
}