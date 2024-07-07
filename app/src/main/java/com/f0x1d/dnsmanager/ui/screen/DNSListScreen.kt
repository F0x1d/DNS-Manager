package com.f0x1d.dnsmanager.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.model.navigation.Screen
import com.f0x1d.dnsmanager.ui.widget.BottomEdgeFloatingActionView
import com.f0x1d.dnsmanager.ui.widget.DNSItem
import com.f0x1d.dnsmanager.viewmodel.DNSListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DNSListScreen(navController: NavController) {
    val viewModel = hiltViewModel<DNSListViewModel>()

    val listItems by viewModel.dnsItems.collectAsStateWithLifecycle(initialValue = emptyList())
    val selectedDNSHost by viewModel.currentDNSHost.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            actions = {
                IconButton(onClick = { viewModel.reset() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sync),
                        contentDescription = null
                    )
                }

                IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = null
                    )
                }

                var menuExpanded by remember { mutableStateOf(false) }

                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_vert),
                        contentDescription = null,
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    val exportLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.CreateDocument("application/json"),
                    ) { uri ->
                        viewModel.export(uri ?: return@rememberLauncherForActivityResult)
                    }
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.export_hosts)) },
                        onClick = { exportLauncher.launch("hosts.json") },
                    )

                    val importLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent(),
                    ) { uri ->
                        viewModel.import(uri ?: return@rememberLauncherForActivityResult)
                    }
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.import_hosts)) },
                        onClick = { importLauncher.launch("*/*") },
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = bottomPaddingForFAB()
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = listItems,
                    key = { it.id }
                ) {
                    DNSItem(
                        item = it,
                        selectedHost = selectedDNSHost,
                        onClick = { viewModel.select(it) },
                        onLongClick = {
                            navController.navigate("${Screen.CreateDNSItem.route}/${it.id}")
                        },
                        onDelete = { viewModel.delete(it) }
                    )
                }
            }

            BottomEdgeFloatingActionView(icon = R.drawable.ic_add) {
                navController.navigate(Screen.CreateDNSItem.route)
            }
        }
    }

    LifecycleStartEffect(Unit) {
        viewModel.updateSelectedDNSHost()
        onStopOrDispose {}
    }
}

@Composable
fun bottomPaddingForFAB() = 88.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
