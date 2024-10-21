package com.ankn.features.home

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ankn.core.ui.component.AppTopBar
import com.ankn.core.ui.component.SetStatusBarColor
import com.ankn.features.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    SetStatusBarColor(darkIcons = false)

    val uiState by viewModel.uiState.collectAsState()

    val notificationPermissionState = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    ) { isGranted ->
        viewModel.updateNotificationPermissionState(isGranted)
    }

    LaunchedEffect(key1 = Unit) {
        // Check permission state when the screen is first composed
        if (!uiState.hasNotificationPermission) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    HomeContent(
        uiState = uiState,
        onSwitchToggle = {
            //   viewModel.showNotificationTest()
            if (uiState.hasNotificationPermission) {
                viewModel.updateSettings(!uiState.isReminderEnabled, uiState.reminderSettings.intervalMinutes)
            } else {
                notificationPermissionState.launchPermissionRequest()
            }
        },
        onRequestPermission = {
            notificationPermissionState.launchPermissionRequest()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onSwitchToggle: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(stringResource(R.string.home))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.content_title),
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.start_stop_button),
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clickable(onClick = onSwitchToggle)
            ) {
                Image(
                    painter = painterResource(
                        id = if (uiState.isReminderEnabled) {
                            R.drawable.ic_switch_on
                        } else {
                            R.drawable.ic_switch_off
                        }
                    ),
                    contentDescription = if (uiState.isReminderEnabled) "Turn off reminders" else "Turn on reminders",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = uiState.statusText,
                modifier = Modifier.padding(top = 16.dp),
                textAlign = TextAlign.Center
            )

            if (!uiState.hasNotificationPermission) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Notification permission is required for reminders",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
