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
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flaze.trazer.composable.AuthScreen
import com.flaze.trazer.composable.MessageScreen
import com.flaze.trazer.composable.OverviewScreen
import com.flaze.trazer.composable.SettingsScreen
import com.flaze.trazer.model.AuthViewModel
import com.flaze.trazer.repository.SettingsRepository
import com.flaze.trazer.ui.theme.TrazerTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val authViewModel = AuthViewModel()
        setContent {
            enableEdgeToEdge()
            setContent {
                TrazerTheme {
                    Layout(authViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Layout(authViewModel: AuthViewModel) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val appName = stringResource(id = R.string.app_name)
    val settingsRepository = SettingsRepository(LocalContext.current)
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    val startDestination = if (isLoggedIn) "message" else "auth"

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(appName, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(icon = {
                    Icon(
                        Icons.Filled.Insights, contentDescription = "Insights"
                    )
                }, label = { Text("Insights") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("overview")
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
            TopAppBar(title = {
                Text(
                    currentDestination?.uppercase() ?: appName
                )
            }, navigationIcon = {
                if (isLoggedIn) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open drawer")
                    }
                }
            }, actions = {
                // Add Search Icon
                IconButton(onClick = {
                    // Define search act
                }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            })
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("auth") {
                    AuthScreen(authViewModel) {
                        navController.popBackStack()
                        navController.navigate("message")
                    }
                }
                composable("overview") { OverviewScreen() }
                composable("message") { MessageScreen(settingsRepository) }
                composable("settings") {
                    SettingsScreen(
                        settingsRepository, authViewModel, navController
                    )
                }
            }
        }
    })
}
