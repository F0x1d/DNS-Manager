package com.f0x1d.dnsmanager.ui.screen

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.f0x1d.dnsmanager.R
import dev.shreyaspatil.permissionFlow.PermissionFlow

private val COMMAND = "pm grant com.f0x1d.dnsmanager ${Manifest.permission.WRITE_SECURE_SETTINGS}"

@Composable
fun SetupScreen() {
    var infoOpened by rememberSaveable { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            onClick = { infoOpened = true }
        ) {
            Text(text = stringResource(id = R.string.how_to_grant_permission))
        }
        
        if (infoOpened) {
            val context = LocalContext.current

            AlertDialog(
                onDismissRequest = { infoOpened = false }, 
                confirmButton = {
                    TextButton(onClick = {
                        infoOpened = false
                        checkPermission()
                    }) {
                        Text(text = stringResource(id = R.string.check))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        infoOpened = false
                        context.copyText(COMMAND)
                    }) {
                        Text(text = stringResource(id = android.R.string.copy))
                    }
                },
                title = { Text(text = stringResource(id = R.string.how_to_grant_permission)) },
                text = { Text(text = stringResource(id = R.string.how_to_grant_permission_answer, COMMAND)) }
            )
        }
    }
}

private fun checkPermission() = PermissionFlow
    .getInstance()
    .notifyPermissionsChanged(Manifest.permission.WRITE_SECURE_SETTINGS)

private fun Context.copyText(text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText("command", text))
}