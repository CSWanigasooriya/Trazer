package com.flaze.trazer.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flaze.trazer.model.Message
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
fun MessageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val messages = remember { mutableStateListOf<Message>() }
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { }
        val snackBarHostState = remember { SnackbarHostState() }

        val permissionState = rememberPermissionState(
            permission = Manifest.permission.READ_SMS
        )
        var showRationale by remember { mutableStateOf(false) }

        // Check permission status and show rationale if needed
        LaunchedEffect(permissionState.status) {
            if (permissionState.status.shouldShowRationale) {
                showRationale = true
            } else if (!permissionState.status.isGranted) {
                // Permission denied, show snack bar
                snackBarHostState.showSnackbar(
                    message = "SMS permission is required for this feature.",
                    duration = SnackbarDuration.Short
                )
            }
        }

        // Request permission if rationale is shown or permission is not granted
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

        // Permission granted, proceed with reading SMS messages
        if (permissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                val smsMessages = readSmsMessages(context)
                messages.addAll(smsMessages)
            }
        } else {
            LaunchedEffect(Unit) {
                snackBarHostState.showSnackbar(
                    message = "SMS permission is required for this feature to work.",
                    duration = SnackbarDuration.Short
                )
            }
            Button(onClick = {
                // Redirect to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                launcher.launch(intent)
            }) {
                Text("Go to Settings")
            }
        }

        SmsMessageList(messages)

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

