package com.flaze.trazer.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.flaze.trazer.model.AuthViewModel
import com.flaze.trazer.repository.SettingsRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    // Load initial values from SharedPreferences
    var sender by remember { mutableStateOf(settingsRepository.getSender()) }
    var regexPattern by remember { mutableStateOf(settingsRepository.getRegexPattern()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(value = sender, onValueChange = {
            sender = it
            settingsRepository.saveSender(it)  // Save to SharedPreferences
        }, label = { Text("Sender") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = regexPattern,
            onValueChange = {
                regexPattern = it
                settingsRepository.saveRegexPattern(it)  // Save to SharedPreferences
            },
            label = { Text("Regex Pattern") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                authViewModel.signOut()
                navController.navigate("auth") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            }
        }) {
            Text(text = "Sign Out")
        }
    }
}