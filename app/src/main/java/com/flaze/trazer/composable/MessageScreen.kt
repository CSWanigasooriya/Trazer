package com.flaze.trazer.composable

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flaze.trazer.model.Message
import com.flaze.trazer.repository.SettingsRepository
import com.flaze.trazer.util.readSmsMessages
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MessageScreen(settingsRepository: SettingsRepository) {
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<Message>() }
    var searchQuery by remember { mutableStateOf("") }
    var filteredMessages = remember(searchQuery, messages) {
        if (searchQuery.isEmpty()) messages else messages.filter {
            it.body!!.contains(searchQuery, ignoreCase = true)
        }
    }

    if (settingsRepository.getSender().isNotEmpty()) {
        filteredMessages = filteredMessages.filter {
            it.address!!.contains(settingsRepository.getSender())
        }
    }

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.READ_SMS
    )
    var showRationale by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    // Handle permission-related logic
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.shouldShowRationale) {
            showRationale = true
        } else if (!permissionState.status.isGranted) {
            snackBarHostState.showSnackbar(
                message = "SMS permission is required for this feature.",
                duration = SnackbarDuration.Short
            )
        }
    }

    if (showRationale || !permissionState.status.isGranted) {
        AlertDialog(onDismissRequest = { showRationale = false },
            title = { Text("SMS Permission Required") },
            text = { Text("This app needs access to your SMS messages to function properly.") },
            confirmButton = {
                Button(onClick = {
                    permissionState.launchPermissionRequest()
                    showRationale = false
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showRationale = false
                    (context as? Activity)?.finish()
                }) {
                    Text("Cancel")
                }
            })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (permissionState.status.isGranted) {
                LaunchedEffect(Unit) {
                    val smsMessages = readSmsMessages(context)
                    messages.addAll(smsMessages)
                }
            } else {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }) {
                    Text("Go to Settings")
                }
            }

            // Display filtered messages
            SmsMessageList(filteredMessages)
        }

        // Add the OutlinedTextField for filtering at the bottom with a solid background
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Solid background color
                .padding(vertical = 16.dp)
        ) {
            OutlinedTextField(value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Filter Messages") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun SmsMessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    var expanded by remember { mutableStateOf(false) } // State to track the expansion

    OutlinedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main content (always visible)
            Text(text = "ID: ${message.id}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "From: ${message.address}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Message: ${message.body}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Format date to a more readable format
            val formattedDate = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            ).format(Date(message.date))
            Text(text = "Date: $formattedDate", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Clickable to toggle the expandable section
            Text(text = if (expanded) "Hide Details" else "Show Details",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(vertical = 8.dp))

            // Expanded section - displayed when expanded is true
            if (expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(text = "Type: ${message.type}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Thread ID: ${message.threadId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Displaying additional message details
                    Text(
                        text = "Error Code: ${message.errorCode}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Locked: ${if (message.locked == 1) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Protocol: ${message.protocol}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Read: ${if (message.read == 1) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Reply Path Present: ${if (message.replyPathPresent == 1) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Seen: ${if (message.seen == 1) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Service Center: ${message.serviceCenter}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Status: ${message.status}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Subject: ${message.subject ?: "No subject"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Subscription ID: ${message.subscriptionId}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

