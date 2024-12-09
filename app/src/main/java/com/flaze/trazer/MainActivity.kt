package com.flaze.trazer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flaze.trazer.fragments.MessageScreen
import com.flaze.trazer.fragments.OverviewScreen
import com.flaze.trazer.fragments.SettingsScreen
import com.flaze.trazer.ui.theme.TrazerTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            setContent {
                TrazerTheme {
                    Layout()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val appName = stringResource(id = R.string.app_name)

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(appName, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(icon = {
                    Icon(
                        Icons.Filled.Home, contentDescription = "Home"
                    )
                }, label = { Text("Home") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("home")
                })
                NavigationDrawerItem(icon = {
                    Icon(
                        Icons.Filled.Settings, contentDescription = "Settings"
                    )
                }, label = { Text("Settings") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("settings")
                })
            }
        }
    }, content = {
        Scaffold(topBar = {
            TopAppBar(title = { Text(appName) }, navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Open drawer")
                }
            })
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "message",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("overview") { OverviewScreen() }
                composable("message") { (MessageScreen()) }
                composable("settings") { SettingsScreen() }
            }
        }
    })
}
