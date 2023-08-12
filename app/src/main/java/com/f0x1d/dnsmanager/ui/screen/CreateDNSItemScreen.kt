package com.f0x1d.dnsmanager.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.ui.activity.MainActivityEntryPoint
import com.f0x1d.dnsmanager.ui.widget.BottomEdgeFloatingActionView
import com.f0x1d.dnsmanager.ui.widget.NavigationBackIcon
import com.f0x1d.dnsmanager.viewmodel.CreateDNSItemViewModel
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDNSItemScreen(navController: NavController, itemId: Long = -1) {
    val viewModel = createDNSItemViewModel(id = itemId)

    val dnsItem by viewModel.dnsItem.collectAsStateWithLifecycle(initialValue = null)

    var name by rememberSaveable(dnsItem) { mutableStateOf(dnsItem?.name ?: "") }
    var host by rememberSaveable(dnsItem) { mutableStateOf(dnsItem?.host ?: "") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = if (itemId == -1L) R.string.create else R.string.edit)) },
                navigationIcon = { NavigationBackIcon(navController = navController) }
            )

            Column(
                modifier = Modifier.padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.name)) },
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    value = host,
                    onValueChange = { host = it },
                    label = { Text(text = stringResource(id = R.string.host)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        BottomEdgeFloatingActionView(icon = R.drawable.ic_save) {
            viewModel.create(dnsItem, name, host) {
                navController.popBackStack()
            }
        }
    }
}

@Composable
private fun createDNSItemViewModel(id: Long): CreateDNSItemViewModel {
    val context = LocalContext.current
    val assistedFactory = remember {
        EntryPointAccessors.fromActivity(
            context as Activity,
            MainActivityEntryPoint::class.java
        ).createDNSItemViewModelFactory()
    }

    return viewModel(factory = CreateDNSItemViewModel.provideFactory(assistedFactory, id))
}